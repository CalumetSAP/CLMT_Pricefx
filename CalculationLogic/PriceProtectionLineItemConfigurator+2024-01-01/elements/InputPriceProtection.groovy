final lineItem = libs.QuoteConstantsLibrary.LineItem

def readOnly = !api.local.isNotFreightGroup
def options = !api.isInputGenerationExecution() && api.local.priceProtectionOptions ? api.local.priceProtectionOptions : [:]
def defaultValue = api.local.priceProtectionOptions?.find { k, v -> v == out.FindExclusion?.attribute2 }?.key as Object

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItem.PP_CONFIGURATOR_PRICE_PROTECTION_ID,
        lineItem.PP_CONFIGURATOR_PRICE_PROTECTION_LABEL,
        false,
        readOnly,
        defaultValue,
        options
)

return entry