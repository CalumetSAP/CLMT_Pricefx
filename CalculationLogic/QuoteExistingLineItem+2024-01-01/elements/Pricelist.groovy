final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.PRICE_LIST_ID)
            .setLabel(lineItemConstants.PRICE_LIST_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PRICE_LIST_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PRICE_LIST_ID)
}