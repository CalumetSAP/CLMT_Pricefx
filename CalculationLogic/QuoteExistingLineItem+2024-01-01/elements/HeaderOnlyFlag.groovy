final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry(lineItemConstants.HEADER_ONLY_FLAG_ID)
            .setLabel(lineItemConstants.HEADER_ONLY_FLAG_LABEL)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.HEADER_ONLY_FLAG_ID]
}