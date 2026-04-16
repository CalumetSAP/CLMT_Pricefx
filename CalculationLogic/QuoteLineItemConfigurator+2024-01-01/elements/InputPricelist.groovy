if (out.InputPriceType?.getFirstInput()?.getValue() != "3") return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.pricelists && !api.isInputGenerationExecution() ? api.local.pricelists : [:]
def defaultValue = options?.size() == 1 ? options?.keySet()?.find() : null

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.PRICE_LIST_ID,
        lineItemConstants.PRICE_LIST_LABEL,
        true,
        false,
        defaultValue,
        options as Map
)

return entry