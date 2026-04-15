final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.REJECTION_REASON_ID)
            .setLabel(lineItemConstants.REJECTION_REASON_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.REJECTION_REASON_ID]
}