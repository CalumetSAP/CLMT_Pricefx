final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createBooleanUserEntry(lineItemConstants.FREIGHT_ESTIMATE_ID)
            .setLabel(lineItemConstants.FREIGHT_ESTIMATE_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .setValue(false)
            .getInput()
} else {
    return input[lineItemConstants.FREIGHT_ESTIMATE_ID]
}