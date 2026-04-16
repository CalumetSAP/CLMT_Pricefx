final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createStringUserEntry(lineItemConstants.SHIPPING_POINT_ID)
            .setLabel(lineItemConstants.SHIPPING_POINT_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.SHIPPING_POINT_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.SHIPPING_POINT_ID)
}