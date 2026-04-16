final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = libs.BdpLib.UserInputs.createInputCheckbox(
        lineItemConstants.FREIGHT_ESTIMATE_ID,
        lineItemConstants.FREIGHT_ESTIMATE_LABEL,
        true,
        false,
        false
)

return entry