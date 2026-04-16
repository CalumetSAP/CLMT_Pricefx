final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = out.InputPriceType?.getFirstInput()?.getValue() == "2"

def entry = libs.BdpLib.UserInputs.createInputDecimal(
        lineItemConstants.PER_ID,
        lineItemConstants.PER_LABEL,
        required,
        false
)

return entry