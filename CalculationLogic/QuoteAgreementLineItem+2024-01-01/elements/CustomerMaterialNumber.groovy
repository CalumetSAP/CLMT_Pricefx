final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createStringUserEntry(lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID)
            .setLabel(lineItemConstants.CUSTOMER_MATERIAL_NUMBER_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID]
}