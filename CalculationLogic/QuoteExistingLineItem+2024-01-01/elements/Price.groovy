final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createUserEntry(lineItemConstants.PRICE_ID)
            .setLabel(lineItemConstants.PRICE_LABEL)
            .setDataType("float")
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PRICE_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PRICE_ID)
}