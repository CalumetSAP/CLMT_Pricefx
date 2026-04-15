if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def required = out.InputMovementTiming?.getFirstInput()?.getValue() == "Quarter"
def options = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.MOVEMENT_START_ID,
        headerConstants.MOVEMENT_START_LABEL,
        required,
        false,
        options
)

return entry