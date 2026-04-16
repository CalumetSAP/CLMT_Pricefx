final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createDateUserEntry(lineItemConstants.PRICE_VALID_TO_ID)
            .setLabel(lineItemConstants.PRICE_VALID_TO_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PRICE_VALID_TO_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PRICE_VALID_TO_ID)
}