final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = libs.BdpLib.UserInputs.createInputString(
        lineItemConstants.MATERIAL_ID,
        lineItemConstants.MATERIAL_LABEL,
        false,
        true,
        api.local.sku as String
)

return entry