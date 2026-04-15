final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.FREIGHT_TERM_ID)
            .setLabel(lineItemConstants.FREIGHT_TERM_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.FREIGHT_TERM_ID]
}