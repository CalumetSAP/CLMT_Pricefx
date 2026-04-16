final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = [
        "Month",
        "Quarter"
]
def required = out.InputPriceProtection?.getFirstInput()?.getValue() == "Moves on a specified day"

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.MOVEMENT_TIMING_ID,
        headerConstants.MOVEMENT_TIMING_LABEL,
        required,
        false,
        options
)

return entry