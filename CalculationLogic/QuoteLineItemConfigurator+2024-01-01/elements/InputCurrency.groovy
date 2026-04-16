final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.currency && !api.isInputGenerationExecution() ? api.local.currency : [:]

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.CURRENCY_ID,
        lineItemConstants.CURRENCY_LABEL,
        true,
        false,
        null,
        options as Map
)

return entry