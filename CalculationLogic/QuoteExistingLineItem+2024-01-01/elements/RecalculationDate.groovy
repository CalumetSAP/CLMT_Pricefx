final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.isInputGenerationExecution()) {
    def options = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                   "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
                   "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]

    api.inputBuilderFactory().createOptionEntry(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID)
            .setLabel(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_LABEL)
            .setRequired(false)
            .setReadOnly(false)
            .setOptions(options)
            .getInput()
} else {
    return input[lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID] ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID)
}