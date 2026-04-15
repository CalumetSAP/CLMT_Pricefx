final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry(lineItemConstants.PRICE_CHANGE_FLAG_ID)
            .setLabel(lineItemConstants.PRICE_CHANGE_FLAG_LABEL)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.PRICE_CHANGE_FLAG_ID]
}