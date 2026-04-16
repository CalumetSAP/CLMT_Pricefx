if (api.isInputGenerationExecution()) return null

def quote = api.currentItem()

if (quote?.get("quoteType") != "ExistingContractUpdate") return null

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

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
        allPlants.add(getOutputByName(line.get("outputs"), "Plant")?.split("-")?.first()?.trim())
        allShipTos.add(getOutputByName(line.get("outputs"), "ShipTo") as String)
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

    def materialAndLabel = []
    if (line.label != null) materialAndLabel << line.label
    if (line.sku != null) materialAndLabel << line.sku
    materialAndLabel = materialAndLabel.join(" / ")

    def customerMaterialNumber = []
    if (calculations.getInputValue(line, lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID) != null) customerMaterialNumber << calculations.getInputValue(line, lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID)
    if (calculations.getInputValue(line, lineItemConstants.THIRD_PARTY_CUSTOMER_ID) != null) customerMaterialNumber << calculations.getInputValue(line, lineItemConstants.THIRD_PARTY_CUSTOMER_ID)
    customerMaterialNumber = customerMaterialNumber.join(" / ")

    def plant = getOutputByName(line.get("outputs"), "Plant")?.split("-")?.first()?.trim() ?: ""
    def plantData = plantDataMap[plant]
    def origin = plantData ? "${plantData.attribute10}, ${plantData.attribute18}" : ""

    def shipTo = getOutputByName(line.get("outputs"), "ShipTo")
    def shipToData = shipToDataMap[shipTo]
    def deliveredLocation = shipToData ? "${shipToData.name}, ${shipToData.attribute5}, ${shipToData.attribute7}" : ""

//    def modeOfSale = configurator.SalesShippingMethodInput ?: ""

    def moq = calculations.getInputValue(line, lineItemConstants.MOQ_ID) ?: ""

    def salesUom = uomDescription?.get(calculations.getInputValue(line, lineItemConstants.MOQ_UOM_ID)) ? uomDescription?.get(calculations.getInputValue(line, lineItemConstants.MOQ_UOM_ID)) : calculations.getInputValue(line, lineItemConstants.MOQ_UOM_ID) ?: ""

    def wpg = outputs.WeightPerGallon ?: ""

    def price = calculations.getInputValue(line, lineItemConstants.PRICE_ID) ?: ""
    def numberOfDecimals = calculations.getInputValue(line, lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: ""
    if(price != ""){
        if(numberOfDecimals.isInteger()) {
            def formatString = "#####0." + "0" * numberOfDecimals?.toInteger()
            price = api.formatNumber(formatString, calculations.getInputValue(line, lineItemConstants.PRICE_ID))
        } else {
            price = api.formatNumber("#####0.00", calculations.getInputValue(line, lineItemConstants.PRICE_ID))
        }
    }

    def pricingUom = uomDescription?.get(calculations.getInputValue(line, lineItemConstants.PRICING_UOM_ID)) ? uomDescription?.get(calculations.getInputValue(line, lineItemConstants.PRICING_UOM_ID)) : calculations.getInputValue(line, lineItemConstants.PRICING_UOM_ID) ?: ""

    def freightTerm = calculations.getInputValue(line, lineItemConstants.FREIGHT_TERM_ID) ?: ""

    def priceType = calculations.getInputValue(line, lineItemConstants.PRICE_TYPE_ID)
    priceType = priceType ? getPriceTypeValues()?.get(priceType) : priceType

    def isIndexed = priceType == "1" ? getNextIndex() : ""

    def row = [
            materialAndLabel        : materialAndLabel,
            customerMaterialNumber  : customerMaterialNumber,
            origin                  : origin,
            deliveredLocation       : deliveredLocation,
//            modeOfSale              : modeOfSale,
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

def getOutputByName(outputs, name) {
    return outputs?.find { it.resultName == name}?.result
}

def getPriceTypeValues() {
    def tablesConstants = libs.QuoteConstantsLibrary.Tables

    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.in("key2", ["PriceType"]),
    ]
    return api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)?.collectEntries {
        [(it.attribute1) : it.key3]
    }
}