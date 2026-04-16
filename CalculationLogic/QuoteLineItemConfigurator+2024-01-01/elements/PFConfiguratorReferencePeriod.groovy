if (out.InputPriceType?.getFirstInput()?.getValue() != "1") return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = !api.isInputGenerationExecution() && api.local.referencePeriod ? api.local.referencePeriod : [:]

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID,
        lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_LABEL,
        true,
        false,
        null,
        options as Map
)

return entry