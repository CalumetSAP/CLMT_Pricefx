final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.MEANS_OF_TRANSPORTATION_ID)
            .setLabel(lineItemConstants.MEANS_OF_TRANSPORTATION_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.MEANS_OF_TRANSPORTATION_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.MEANS_OF_TRANSPORTATION_ID)
}