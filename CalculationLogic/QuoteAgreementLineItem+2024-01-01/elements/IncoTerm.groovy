final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.INCO_TERM_ID)
            .setLabel(lineItemConstants.INCO_TERM_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.INCO_TERM_ID]
}