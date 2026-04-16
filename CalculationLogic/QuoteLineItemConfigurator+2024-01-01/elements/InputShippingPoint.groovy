final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = out.FindPlantShippingPoint && !api.isInputGenerationExecution() ? out.FindPlantShippingPoint?.findAll()?.sort() : []
def defaultValue = options?.size() == 1 ? options?.find() : null

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.SHIPPING_POINT_ID,
        lineItemConstants.SHIPPING_POINT_LABEL,
        true,
        false,
        options as List,
        defaultValue
)

return entry