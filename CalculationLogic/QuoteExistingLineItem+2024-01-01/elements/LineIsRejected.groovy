final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createBooleanUserEntry(lineItemConstants.LINE_IS_REJECTED_ID)
            .setLabel(lineItemConstants.LINE_IS_REJECTED_LABEL)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.LINE_IS_REJECTED_ID]
}