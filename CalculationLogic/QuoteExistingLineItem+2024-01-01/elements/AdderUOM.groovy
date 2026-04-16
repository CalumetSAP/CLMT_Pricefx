final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createOptionEntry(lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID)
            .setLabel(lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID)
}