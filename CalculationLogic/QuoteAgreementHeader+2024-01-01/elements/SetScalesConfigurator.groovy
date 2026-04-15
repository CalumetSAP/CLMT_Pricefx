import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def uomOptions = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions as Map : [:]

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def uomsPerMaterial = uomOptions?.getOrDefault(lnProduct.sku, [])
    uomsPerMaterial?.add("KG")
    uomsPerMaterial?.sort()
    uomsPerMaterial?.unique()

    def params = [
            UOM: uomsPerMaterial
    ]

    def previousValues = lnProduct.inputs.find {
        lineItemConstants.SCALES_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
            "name"    : lineItemConstants.SCALES_CONFIGURATOR_NAME,
            "label"   : lineItemConstants.SCALES_CONFIGURATOR_NAME,
            "type"    : InputType.CONFIGURATOR,
            "url"     : lineItemConstants.SCALES_CONFIGURATOR_URL,
            "readOnly": false,
            "value"   : previousValues + params,
    ])
}

return null
