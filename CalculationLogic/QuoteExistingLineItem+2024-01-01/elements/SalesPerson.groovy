final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.SALES_PERSON_ID)
            .setLabel(lineItemConstants.SALES_PERSON_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.SALES_PERSON_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.SALES_PERSON_ID)
}