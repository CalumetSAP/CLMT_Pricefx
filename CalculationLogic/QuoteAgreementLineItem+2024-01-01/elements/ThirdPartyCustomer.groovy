final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createStringUserEntry(lineItemConstants.THIRD_PARTY_CUSTOMER_ID)
            .setLabel(lineItemConstants.THIRD_PARTY_CUSTOMER_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.THIRD_PARTY_CUSTOMER_ID]
}