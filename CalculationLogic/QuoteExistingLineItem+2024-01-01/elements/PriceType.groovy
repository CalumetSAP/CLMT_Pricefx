final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.PRICE_TYPE_ID)
            .setLabel(lineItemConstants.PRICE_TYPE_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PRICE_TYPE_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PRICE_TYPE_ID)
}