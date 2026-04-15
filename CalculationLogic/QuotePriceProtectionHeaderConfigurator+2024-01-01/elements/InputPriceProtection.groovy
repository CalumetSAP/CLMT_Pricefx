final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = !api.isInputGenerationExecution() && api.local.priceProtectionOptions ? api.local.priceProtectionOptions : [:]

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.PRICE_PROTECTION_ID,
        headerConstants.PRICE_PROTECTION_LABEL,
        false,
        false,
        null,
        options
)

return entry