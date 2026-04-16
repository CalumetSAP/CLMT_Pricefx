if (out.InputPriceType?.getFirstInput()?.getValue() != "1") return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = !api.isInputGenerationExecution() && api.local.recalculationPeriodOptions ? api.local.recalculationPeriodOptions as Map : [:]

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID,
        lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_LABEL,
        true,
        false,
        null,
        options
)

return entry