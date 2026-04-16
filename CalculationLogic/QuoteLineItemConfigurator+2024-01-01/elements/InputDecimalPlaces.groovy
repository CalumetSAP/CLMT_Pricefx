final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.decimalPlaces && !api.isInputGenerationExecution() ? api.local.decimalPlaces : [:]

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.NUMBER_OF_DECIMALS_ID,
        lineItemConstants.NUMBER_OF_DECIMALS_LABEL,
        true,
        false,
        null,
        options as Map
)

return entry