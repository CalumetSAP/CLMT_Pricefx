if (out.InputPriceType?.getFirstInput()?.getValue() != "1") return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.decimalPlaces && !api.isInputGenerationExecution() ? api.local.decimalPlaces : [:]

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.PF_CONFIGURATOR_ADDER_NUMBER_OF_DECIMAL_PLACES_ID,
        lineItemConstants.PF_CONFIGURATOR_ADDER_NUMBER_OF_DECIMAL_PLACES_LABEL,
        true,
        false,
        null,
        options as Map
)

return entry