final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    def defaultValue = api.local.product?.attribute9 ? api.local.product?.attribute9 as String : api.local.product?.unitOfMeasure as String
    def options = defaultValue ? [defaultValue] : []
    api.local.pricingUOMDefaultValue = defaultValue

    api.inputBuilderFactory().createOptionEntry(lineItemConstants.PRICING_UOM_ID)
            .setLabel(lineItemConstants.PRICING_UOM_LABEL)
            .setOptions(options)
            .setRequired(false)
            .setReadOnly(false)
            .setValue(defaultValue)
            .getInput()
} else {
    return input[lineItemConstants.PRICING_UOM_ID]
}