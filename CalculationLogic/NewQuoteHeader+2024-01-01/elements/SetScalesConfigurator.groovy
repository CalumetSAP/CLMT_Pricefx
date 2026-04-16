import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def globalUOMConversionMap = out.FindGlobalUOMConversionTable ?: [:]
def uomConversionMap = out.FindUOMConversionTable ?: [:]

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def salesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)

def uomOptions = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions as Map : [:]

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)
    def selectedMOQUOM = configurator?.get(lineItemConstants.MOQ_UOM_ID)
    def selectedPriceUOM = configurator?.get(lineItemConstants.PRICING_UOM_ID)
    def selectedPriceType = configurator?.get(lineItemConstants.PRICE_TYPE_ID)
    def numberOfDecimals = configurator?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: 2
    def previousPriceType = calculations.getInputValue(lnProduct, lineItemConstants.PREVIOUS_PRICE_TYPE_ID)
    def priceType = configurator?.get(lineItemConstants.PRICE_TYPE_ID)
    def pricelist = configurator?.get(lineItemConstants.PRICE_LIST_ID)
    def moq = configurator?.get(lineItemConstants.MOQ_ID)
    def priceValidFrom = configurator?.get(lineItemConstants.PRICE_VALID_FROM_ID)

    def uomsPerMaterial = uomOptions?.getOrDefault(lnProduct.sku, [])
    uomsPerMaterial?.add("KG")
    uomsPerMaterial?.sort()
    uomsPerMaterial?.unique()

    def params = [
            UOM             : uomsPerMaterial,
            MOQUOM          : selectedMOQUOM,
            PriceUOM        : selectedPriceUOM,
            PriceType       : selectedPriceType,
            NumberOfDecimals: numberOfDecimals?.toInteger()
    ]

    def previousValues = lnProduct.inputs.find {
        lineItemConstants.SCALES_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    if (priceType != previousPriceType) previousValues.put(lineItemConstants.SCALES_ID, [])

    if (pricelist && priceType == "3") {
        def key = salesOrg + "|" + pricelist + "|" + lnProduct?.sku
        def pricingData = out.ZBPLMerged?.get(key) ?: []
        pricingData = pricingData?.max { it.ValidFrom }
        def scalesData = calculations.getScalesData(pricingData, out.FindZBPLScales)
        if (scalesData) {
            def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(lnProduct?.sku, pricingData?.ScaleUOM, selectedMOQUOM, uomConversionMap, globalUOMConversionMap) ?: 1
            def pricingScales = []
            scalesData?.each { data ->
                if ((data.ScaleQuantity * conversionFactor) >= moq) {
                    def scale = [
                            ScaleQty: data.ScaleQuantity,
                            ScaleUOM: pricingData?.ScaleUOM,
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
            "readOnly": selectedPriceType != "2",
            "value"   : previousValues + params,
    ])

    // Previous Price Type
    quoteProcessor.addOrUpdateInput(
            lnProduct.lineId as String, [
            "name"        : lineItemConstants.PREVIOUS_PRICE_TYPE_ID,
            "value"       : priceType,
    ])
}

return null
