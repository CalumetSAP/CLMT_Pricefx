final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = libs.BdpLib.UserInputs.createInputString(
        lineItemConstants.DESCRIPTION_ID,
        lineItemConstants.DESCRIPTION_LABEL,
        false,
        true,
        api.local.description as String
)

return entry