final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createIntegerUserEntry(lineItemConstants.PER_ID)
            .setLabel(lineItemConstants.PER_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PER_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PER_ID)
}