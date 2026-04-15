final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    def defaultValue = api.local.pricingUOMDefaultValue
    def options = defaultValue ? [defaultValue] : []

    api.inputBuilderFactory().createOptionEntry(lineItemConstants.FREIGHT_UOM_ID)
            .setLabel(lineItemConstants.FREIGHT_UOM_LABEL)
            .setOptions(options)
            .setRequired(false)
            .setReadOnly(true)
            .setValue(defaultValue)
            .getInput()
} else {
    return input[lineItemConstants.FREIGHT_UOM_ID]
}