final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = out.InputPriceType?.getFirstInput()?.getValue() == "2"

def entry = libs.BdpLib.UserInputs.createInputDate(
        lineItemConstants.PRICE_VALID_FROM_ID,
        lineItemConstants.PRICE_VALID_FROM_LABEL,
        required,
        false,
        api.local.validFromDate
)

return entry