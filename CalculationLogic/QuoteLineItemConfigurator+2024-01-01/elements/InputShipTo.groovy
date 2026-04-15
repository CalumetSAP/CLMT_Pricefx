final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.shipTo && !api.isInputGenerationExecution() ? api.local.shipTo : []
def defaultValue = options?.size() == 1 ? options?.find() : null

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.SHIP_TO_ID,
        lineItemConstants.SHIP_TO_LABEL,
        true,
        false,
        options,
        defaultValue
)

return entry