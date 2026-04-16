final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createStringUserEntry(lineItemConstants.NAMED_PLACE_ID)
            .setLabel(lineItemConstants.NAMED_PLACE_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.NAMED_PLACE_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.NAMED_PLACE_ID)
}