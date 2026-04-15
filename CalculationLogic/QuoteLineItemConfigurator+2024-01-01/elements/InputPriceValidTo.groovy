final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = out.InputPriceType?.getFirstInput()?.getValue() == "2"

def entry = libs.BdpLib.UserInputs.createInputDate(
        lineItemConstants.PRICE_VALID_TO_ID,
        lineItemConstants.PRICE_VALID_TO_LABEL,
        required,
        false,
        api.local.validToDate
)

return entry