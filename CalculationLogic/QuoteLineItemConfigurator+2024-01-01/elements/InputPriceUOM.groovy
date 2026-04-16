final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.priceUOM && !api.isInputGenerationExecution() ? api.local.priceUOM : []
def required = out.InputPriceType?.getFirstInput()?.getValue() == "2"
def defaultValue = api.local.attribute9 ? api.local.attribute9 as String : api.local.unitOfMeasure as String

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.PRICING_UOM_ID,
        lineItemConstants.PRICING_UOM_LABEL,
        required,
        false,
        options as List,
        defaultValue
)

return entry