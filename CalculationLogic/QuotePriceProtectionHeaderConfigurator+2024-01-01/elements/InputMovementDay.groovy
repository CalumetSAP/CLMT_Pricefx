final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def required = out.InputPriceProtection?.getFirstInput()?.getValue() == "Moves on a specified day"

def entry = libs.BdpLib.UserInputs.createInputNumber(
        headerConstants.MOVEMENT_DAY_ID,
        headerConstants.MOVEMENT_DAY_LABEL,
        required,
        false
)

return entry