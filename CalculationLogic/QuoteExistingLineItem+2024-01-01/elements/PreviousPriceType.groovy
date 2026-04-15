final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry(lineItemConstants.PREVIOUS_PRICE_TYPE_ID)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.PREVIOUS_PRICE_TYPE_ID]
}