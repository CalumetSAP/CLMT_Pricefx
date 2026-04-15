final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createDateUserEntry(lineItemConstants.FREIGHT_VALID_TO_ID)
            .setLabel(lineItemConstants.FREIGHT_VALID_TO_LABEL)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.FREIGHT_VALID_TO_ID]
}