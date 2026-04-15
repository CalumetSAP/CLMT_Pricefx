final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.SHIP_TO_ID)
            .setLabel(lineItemConstants.SHIP_TO_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.SHIP_TO_ID]
}