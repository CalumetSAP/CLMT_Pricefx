final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry(lineItemConstants.APPROVER_HIDDEN_ID)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.APPROVER_HIDDEN_ID]
}