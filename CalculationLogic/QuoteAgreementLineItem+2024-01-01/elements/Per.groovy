final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createUserEntry(lineItemConstants.PER_ID)
            .setLabel(lineItemConstants.PER_LABEL)
            .setDataType("float")
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PER_ID]
}