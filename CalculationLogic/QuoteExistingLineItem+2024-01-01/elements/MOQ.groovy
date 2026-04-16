final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createIntegerUserEntry(lineItemConstants.MOQ_ID)
            .setLabel(lineItemConstants.MOQ_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.MOQ_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.MOQ_ID)
}