final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.priceTypes && !api.isInputGenerationExecution() ? api.local.priceTypes : [:]
def defaultValue = api.local.PPDefaultValues?.PriceProtection != null ? "2" : null

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.PRICE_TYPE_ID,
        lineItemConstants.PRICE_TYPE_LABEL,
        true,
        false,
        defaultValue,
        options as Map
)

return entry