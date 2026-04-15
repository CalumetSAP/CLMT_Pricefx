import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]
def uomOptions = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions as Map : [:]
def globalUOMConversionMap = out.FindGlobalUOMConversionTable ?: [:]
def uomConversionMap = out.FindUOMConversionTable ?: [:]

def conversionFactor

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    def useConfiguratorValue = api.local.lineItemChanged == lnProduct.lineId

    if (!priceTypeHasChanged(lnProduct, dropdownOptions["PriceType"], useConfiguratorValue)) continue

    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    def dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    def priceType = configurator?.get(lineItemConstants.PRICE_TYPE_ID)
    def pricelist = findValue(useConfiguratorValue, lineItemConstants.PRICE_LIST_ID, lnProduct, configurator)?.split(" - ")?.getAt(0)
    def moqUOM = findValue(useConfiguratorValue, lineItemConstants.MOQ_UOM_ID, lnProduct, configurator)
    def selectedPriceUOM = findValue(useConfiguratorValue, lineItemConstants.PRICING_UOM_ID, lnProduct, configurator)
    def numberOfDecimals = configurator?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: 2
    def moq = findValue(useConfiguratorValue, lineItemConstants.MOQ_ID, lnProduct, configurator)

    def uomsPerMaterial = uomOptions?.getOrDefault(lnProduct.sku, [])
    uomsPerMaterial?.add("KG")
    uomsPerMaterial?.sort()
    uomsPerMaterial?.unique()

    def params = [
            UOM      : priceType == "2" ? uomsPerMaterial : null,
            MOQUOM   : priceType == "2" ? moqUOM : null,
            PriceUOM : priceType == "2" ? selectedPriceUOM : null,
            PriceType: priceType,
            NumberOfDecimals: numberOfDecimals?.toInteger()
    ]

    def previousValues = lnProduct.inputs.find {
        lineItemConstants.SCALES_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]
    previousValues.put(lineItemConstants.SCALES_ID, [])

    if (pricelist || priceType == "2") {
        def key, pricingData, scalesData
        def scaleUOM = null
        if (priceType == "2") {
            key = dsData?.QuoteID + "|" + dsData?.LineId
            pricingData = out.FindQuotesDS?.get(key)
            scalesData = calculations.getScalesData(pricingData, out.FindQuoteScales)
            scaleUOM = scalesData?.find()?.ScaleUOM
        } else if (priceType == "3") {
            key = (dsData.SalesOrg ?: "") + "|" + pricelist + "|" + lnProduct?.sku
            pricingData = out.ZBPLMerged?.get(key)
            scalesData = calculations.getScalesData(pricingData, out.FindZBPLScales)
            scaleUOM = pricingData?.ScaleUOM
        }
        if (scalesData) {
            conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(lnProduct?.sku, scaleUOM, moqUOM, uomConversionMap, globalUOMConversionMap) ?: 1

            def pricingScales = []
            scalesData?.each { data ->
                if ((data.ScaleQuantity * conversionFactor) >= moq) {
                    def scale = [
                            ScaleQty: data.ScaleQuantity,
                            ScaleUOM: scaleUOM,
                            Price   : data.ConditionRate,
                            PriceUOM: pricingData?.UOM,
                    ]
                    pricingScales.add(scale)
                }
            }

            previousValues.put(lineItemConstants.SCALES_ID, pricingScales)
        }
    }

    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
            "name"    : lineItemConstants.SCALES_CONFIGURATOR_NAME,
            "label"   : lineItemConstants.SCALES_CONFIGURATOR_NAME,
            "type"    : InputType.CONFIGURATOR,
            "url"     : lineItemConstants.SCALES_CONFIGURATOR_URL,
            "readOnly": priceType != "2",
            "value"   : previousValues + params,
    ])
}

return

def priceTypeHasChanged(lnProduct, priceTypeMap, useConfiguratorValue) {
    final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
    final calculations = libs.QuoteLibrary.Calculations

    def configuratorValues = lnProduct.inputs.find {
        lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    def priceTypeAux = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID) as String
    def comparePriceType = useConfiguratorValue
            ? configuratorValues?.get(lineItemConstants.PRICE_TYPE_ID)
            : priceTypeAux ? priceTypeMap?.find { k, v -> v.toString().startsWith(priceTypeAux) }?.key : priceTypeAux
    if (comparePriceType == "3") return true
    def previousPriceType = calculations.getInputValue(lnProduct, lineItemConstants.PREVIOUS_PRICE_TYPE_ID)
    if (comparePriceType != previousPriceType) {
        updateValue(lnProduct.lineId, lineItemConstants.PREVIOUS_PRICE_TYPE_ID, comparePriceType)
        return true
    }

    def pricelist = configuratorValues?.get(lineItemConstants.PRICE_LIST_ID)
    def previousPricelist = calculations.getInputValue(lnProduct, lineItemConstants.PREVIOUS_PRICELIST_ID)
    if (pricelist != previousPricelist) {
        updateValue(lnProduct.lineId, lineItemConstants.PREVIOUS_PRICELIST_ID, pricelist)
        return true
    }

    def moq = findValue(useConfiguratorValue, lineItemConstants.MOQ_ID, lnProduct, configuratorValues)
    def previousMOQ = calculations.getInputValue(lnProduct, lineItemConstants.PREVIOUS_MOQ_ID)
    if (moq != previousMOQ) {
        updateValue(lnProduct.lineId, lineItemConstants.PREVIOUS_MOQ_ID, moq)
        return true
    }
    return false
}

def updateValue(String lineId, name, value) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : value,
    ])
}

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}