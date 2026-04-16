final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.PRICING_UOM_ID)
            .setLabel(lineItemConstants.PRICING_UOM_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PRICING_UOM_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PRICING_UOM_ID)
}