final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createUserEntry(lineItemConstants.COMPETITOR_PRICE_ID)
            .setLabel(lineItemConstants.COMPETITOR_PRICE_LABEL)
            .setDataType("float")
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.COMPETITOR_PRICE_ID]
}