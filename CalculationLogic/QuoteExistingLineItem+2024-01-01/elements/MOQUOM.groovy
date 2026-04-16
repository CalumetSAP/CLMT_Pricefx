final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.MOQ_UOM_ID)
            .setLabel(lineItemConstants.MOQ_UOM_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.MOQ_UOM_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.MOQ_UOM_ID)
}