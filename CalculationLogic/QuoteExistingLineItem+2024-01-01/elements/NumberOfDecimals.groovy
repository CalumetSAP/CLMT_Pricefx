final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.NUMBER_OF_DECIMALS_ID)
            .setLabel(lineItemConstants.NUMBER_OF_DECIMALS_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.NUMBER_OF_DECIMALS_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID)
}