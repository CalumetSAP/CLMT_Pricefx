final lineItem = libs.QuoteConstantsLibrary.LineItem

def options = !api.isInputGenerationExecution() && api.local.priceProtectionOptions ? api.local.priceProtectionOptions : [:]
def readOnly = api.local.readOnly ? true : false//api.local.exclusionData ? true : false

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItem.PP_CONFIGURATOR_PRICE_PROTECTION_ID,
        lineItem.PP_CONFIGURATOR_PRICE_PROTECTION_LABEL,
        false,
        readOnly,
        api.local.exclusionData?.PriceProtection as Object,
        options as Map
)

return entry