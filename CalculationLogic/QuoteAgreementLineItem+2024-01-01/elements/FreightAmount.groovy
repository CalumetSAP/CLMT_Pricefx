final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createUserEntry(lineItemConstants.FREIGHT_AMOUNT_ID)
            .setLabel(lineItemConstants.FREIGHT_AMOUNT_LABEL)
            .setDataType("float")
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.FREIGHT_AMOUNT_ID]
}