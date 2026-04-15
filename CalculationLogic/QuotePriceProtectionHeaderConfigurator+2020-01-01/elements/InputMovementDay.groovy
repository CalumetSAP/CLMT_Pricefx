if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def required = out.InputPriceProtection?.getFirstInput()?.getValue() == "3"

def entry = libs.BdpLib.UserInputs.createInputNumber(
        headerConstants.MOVEMENT_DAY_ID,
        headerConstants.MOVEMENT_DAY_LABEL,
        required,
        false
)

return entry