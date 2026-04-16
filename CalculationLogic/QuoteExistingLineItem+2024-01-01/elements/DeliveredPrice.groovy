final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createUserEntry(lineItemConstants.DELIVERED_PRICE_ID)
            .setLabel(lineItemConstants.DELIVERED_PRICE_LABEL)
            .setDataType("float")
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.DELIVERED_PRICE_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.DELIVERED_PRICE_ID)
}