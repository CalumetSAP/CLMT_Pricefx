final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.MODE_OF_TRANSPORTATION_ID)
            .setLabel(lineItemConstants.MODE_OF_TRANSPORTATION_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.MODE_OF_TRANSPORTATION_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.MODE_OF_TRANSPORTATION_ID)
}