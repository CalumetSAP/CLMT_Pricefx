final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createDateUserEntry(lineItemConstants.PRICE_VALID_FROM_ID)
            .setLabel(lineItemConstants.PRICE_VALID_FROM_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PRICE_VALID_FROM_ID]
}