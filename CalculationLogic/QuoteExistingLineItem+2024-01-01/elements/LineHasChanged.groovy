final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry(lineItemConstants.LINE_HAS_CHANGED_ID)
            .setLabel(lineItemConstants.LINE_HAS_CHANGED_LABEL)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.LINE_HAS_CHANGED_ID]
}