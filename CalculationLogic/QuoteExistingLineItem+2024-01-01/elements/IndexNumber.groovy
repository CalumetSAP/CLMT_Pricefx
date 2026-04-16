final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionsEntry(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)
            .setLabel(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)
}