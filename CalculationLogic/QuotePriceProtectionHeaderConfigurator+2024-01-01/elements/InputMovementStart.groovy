final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def required = out.InputMovementTiming?.getFirstInput()?.getValue() == "Quarter"

def entry = libs.BdpLib.UserInputs.createInputNumber(
        headerConstants.MOVEMENT_START_ID,
        headerConstants.MOVEMENT_START_LABEL,
        required,
        false
)

return entry