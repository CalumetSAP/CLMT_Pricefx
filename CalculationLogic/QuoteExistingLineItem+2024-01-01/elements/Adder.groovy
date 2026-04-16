final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createUserEntry(lineItemConstants.PF_CONFIGURATOR_ADDER_ID)
            .setLabel(lineItemConstants.PF_CONFIGURATOR_ADDER_LABEL)
            .setDataType("float")
            .setRequired(false)
            .setReadOnly(false)
            .getInput()
} else {
    return input[lineItemConstants.PF_CONFIGURATOR_ADDER_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_ID)
}