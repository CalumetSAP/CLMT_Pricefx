final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry(lineItemConstants.PT_CHANGED_FROM_INDEX_TO_CUSTOMER_ID)
            .setLabel(lineItemConstants.PT_CHANGED_FROM_INDEX_TO_CUSTOMER_LABEL)
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input[lineItemConstants.PT_CHANGED_FROM_INDEX_TO_CUSTOMER_ID]
}