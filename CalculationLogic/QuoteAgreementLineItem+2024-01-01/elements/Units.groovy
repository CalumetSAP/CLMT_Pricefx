final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createIntegerUserEntry(lineItemConstants.UNITS_ID)
            .setLabel(lineItemConstants.UNITS_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.UNITS_ID]
}