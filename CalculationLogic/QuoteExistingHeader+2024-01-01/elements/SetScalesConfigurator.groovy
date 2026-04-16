import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def uomOptions = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions as Map : [:]

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    def selectedMOQUOM = configurator?.get(lineItemConstants.MOQ_UOM_ID)
    def selectedPriceUOM = configurator?.get(lineItemConstants.PRICING_UOM_ID)
    def selectedPriceType = configurator?.get(lineItemConstants.PRICE_TYPE_ID)
    def numberOfDecimals = configurator?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: 2

    def uomsPerMaterial = uomOptions?.getOrDefault(lnProduct.sku, [])
    uomsPerMaterial?.add("KG")
    uomsPerMaterial?.sort()
    uomsPerMaterial?.unique()

    def params = [
            UOM      : selectedPriceType == "2" ? uomsPerMaterial : null,
            MOQUOM   : selectedPriceType == "2" ? selectedMOQUOM : null,
            PriceUOM : selectedPriceType == "2" ? selectedPriceUOM : null,
            PriceType: selectedPriceType,
            NumberOfDecimals: numberOfDecimals?.toInteger()
    ]

    def previousValues = lnProduct.inputs.find {
        lineItemConstants.SCALES_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    if (selectedPriceType != "2") {
        quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
                "name"    : lineItemConstants.SCALES_CONFIGURATOR_NAME,
                "label"   : lineItemConstants.SCALES_CONFIGURATOR_NAME,
                "type"    : InputType.CONFIGURATOR,
                "url"     : lineItemConstants.SCALES_CONFIGURATOR_URL,
                "readOnly": true,
                "value"   : previousValues + params,
        ])
    } else {
        quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
                "name"    : lineItemConstants.SCALES_CONFIGURATOR_NAME,
                "label"   : lineItemConstants.SCALES_CONFIGURATOR_NAME,
                "type"    : InputType.CONFIGURATOR,
                "url"     : lineItemConstants.SCALES_CONFIGURATOR_URL,
                "readOnly": false,
                "value"   : previousValues + params,
        ])
    }
}

return null
