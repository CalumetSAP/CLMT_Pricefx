final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    def defaultValue = api.local.product?.attribute9 as String
    def options = defaultValue ? [defaultValue] : []

    api.inputBuilderFactory().createOptionEntry(lineItemConstants.SALES_UOM_ID)
            .setLabel(lineItemConstants.SALES_UOM_LABEL)
            .setOptions(options)
            .setRequired(false)
            .setReadOnly(false)
            .setValue(defaultValue)
            .getInput()
} else {
    return input[lineItemConstants.SALES_UOM_ID]
}