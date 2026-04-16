final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry(lineItemConstants.FREIGHT_TERM_CHANGE_FLAG_ID)
            .setLabel(lineItemConstants.FREIGHT_TERM_CHANGE_FLAG_LABEL)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.FREIGHT_TERM_CHANGE_FLAG_ID]
}