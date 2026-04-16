if (api.isInputGenerationExecution()) return null

def quote = api.currentItem()

if (quote?.get("quoteType") != "New Contract" && quote?.get("quoteType") != "NewContract") return null

def rows = []

def uomDescription = api.global.uomDescription

def indexCounter = 0
def getNextIndex = {
    def alphabet = ('A'..'Z').join()
    def result = ""
    def num = indexCounter
    while (num >= 0) {
        result = alphabet[num % 26] + result
        num = (num / 26).intValue() - 1
    }
    indexCounter++
    return result
}

def allPlants = new HashSet<String>()
def allShipTos = new HashSet<String>()

for (line in quote?.lineItems) {
    if (!line.folder) {
        def configurator = line.get("inputs").find { it.name == "Inputs" }?.value ?: [:]
        allPlants.add(configurator.PlantInput?.split("-")?.first()?.trim())
        allShipTos.add(configurator.LineItemShipToInput?.split("-")?.first()?.trim())
    }
}

def plantDataMap = [:]
if (!allPlants.isEmpty()) {
    def plantFilters = [
        Filter.in("name", allPlants)
    ]
    def plantDataList = api.findLookupTableValues("Plant", ["name", "attribute10", "attribute18"], null, *plantFilters)
    plantDataMap = plantDataList.collectEntries { [it.name, it] }
}

def shipToDataMap = [:]
if (!allShipTos.isEmpty()) {
    def shipToFilters = [
        Filter.in("customerId", allShipTos)
    ]
    def shipToDataList = api.find("C", 0, allShipTos.size(), null, ["customerId", "name", "attribute5", "attribute7"], *shipToFilters)
    shipToDataMap = shipToDataList.collectEntries { [it.customerId, it] }
}

for (line in quote?.lineItems) {
    if (line.folder) {
        continue
    }

    def outputs = line.get("outputs").collectEntries { output ->
        [(output.resultName): output.result]
    }

    def configurator = line.get("inputs").find { it.name == "Inputs" }?.value ?: [:]

    def filteredConfigurator = configurator.findAll { key, value ->
        !(key in ['ShipTo', 'Pricelists', 'DropdownOptions', 'IncotermOptions', 'MeansOfTransportationOptions', 'ModeOfTransportationOptions', 'ShippingPointNames', 'SalesPerson', 'UOMTable', 'GlobalUOMTable', 'Guardrails', 'CostPX'])
    }

    filteredConfigurator.each { key, value ->
        if (value instanceof Map) {
            filteredConfigurator[key] = value.findAll { k, v ->
                !(k in ['Guardrails', 'ShippingPointNames', 'SalesPerson', 'IncotermOptions'])
            }
        }
    }

    def materialAndLabel = []
    if (configurator.Product.Description != null) materialAndLabel << configurator.Product.Description
    if (configurator.Product.Material != null) materialAndLabel << configurator.Product.Material
    materialAndLabel = materialAndLabel.join(" / ")

    def customerMaterialNumber = []
    if (configurator.CustomerMaterialNumberInput != null) customerMaterialNumber << configurator.CustomerMaterialNumberInput
    if (configurator.ThirdPartyCustomerInput != null) customerMaterialNumber << configurator.ThirdPartyCustomerInput
    customerMaterialNumber = customerMaterialNumber.join(" / ")

    def plant = configurator.PlantInput?.split("-")?.first()?.trim() ?: ""
    def plantData = plantDataMap[plant]
    def origin = plantData ? "${plantData.attribute10}, ${plantData.attribute18}" : ""

    def shipTo = configurator.LineItemShipToInput?.split("-")?.first()?.trim() ?: ""
    def shipToData = shipToDataMap[shipTo]
    def deliveredLocation = shipToData ? "${shipToData.name}, ${shipToData.attribute5}, ${shipToData.attribute7}" : ""

    def modeOfSale = configurator.SalesShippingMethodInput ?: ""

    def moq = configurator.MOQInput ?: ""
    def salesUom = uomDescription?.get(configurator.MOQUOMInput) ? uomDescription?.get(configurator.MOQUOMInput) : configurator.MOQUOMInput ?: ""

    def wpg = outputs.WeightPerGallon ?: ""

    def price = configurator.PriceInput ?: ""
    def numberOfDecimals = configurator.NumberOfDecimalsInput ?: ""
    if(price != ""){
        if(numberOfDecimals.isInteger()) {
            def formatString = "#####0." + "0" * numberOfDecimals?.toInteger()
            price = api.formatNumber(formatString, configurator.PriceInput)
        } else {
            price = api.formatNumber("#####0.00", configurator.PriceInput)
        }
    }
    def pricingUom = uomDescription?.get(configurator.PricingUOMInput) ? uomDescription?.get(configurator.PricingUOMInput) : configurator.PricingUOMInput ?: ""

    def freightTerms = configurator.DropdownOptions?.FreightTerm
    def freightTerm = freightTerms?.get(configurator.FreightTermInput) ?: ""

    def priceTypes = configurator.DropdownOptions?.PriceType
    def priceType = priceTypes?.get(configurator.PriceTypeInput) ?: ""
    def isIndexed = priceType == "Index" ? getNextIndex() : ""

    def row = [
        materialAndLabel        : materialAndLabel,
        customerMaterialNumber  : customerMaterialNumber,
        origin                  : origin,
        deliveredLocation       : deliveredLocation,
        modeOfSale              : modeOfSale,
        moqUom                  : [moq, salesUom].findAll { it }.join(" / "),
        wpg                     : wpg,
        priceUom                : [price, pricingUom].findAll { it }.join(" / "),
        freight                 : freightTerm,
//        isIndexed               : isIndexed,
    ]

    if (out.hasIndexIndicator) {
        row.isIndexed = isIndexed
    }

    rows << row
}

return rows
