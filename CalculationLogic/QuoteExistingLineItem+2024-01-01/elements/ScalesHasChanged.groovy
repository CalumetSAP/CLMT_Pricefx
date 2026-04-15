final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry(lineItemConstants.SCALES_HAS_CHANGED_ID)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.SCALES_HAS_CHANGED_ID]
}