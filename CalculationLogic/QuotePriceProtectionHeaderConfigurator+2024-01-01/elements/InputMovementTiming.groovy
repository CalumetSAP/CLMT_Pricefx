if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = !api.isInputGenerationExecution() && api.local.movementTimingOptions ? api.local.movementTimingOptions as Map : [:]
def required = out.InputPriceProtection?.getFirstInput()?.getValue() == "3"

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.MOVEMENT_TIMING_ID,
        headerConstants.MOVEMENT_TIMING_LABEL,
        required,
        false,
        null,
        options
)

return entry