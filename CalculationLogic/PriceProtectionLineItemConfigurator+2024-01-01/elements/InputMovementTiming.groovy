if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final lineItem = libs.QuoteConstantsLibrary.LineItem

def options = !api.isInputGenerationExecution() && api.local.movementTimingOptions ? api.local.movementTimingOptions as Map : [:]
def required = out.InputPriceProtection?.getFirstInput()?.getValue() == "3"
def readOnly = api.local.readOnly ? true : false//api.local.exclusionData ? true : false

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItem.PP_CONFIGURATOR_MOVEMENT_TIMING_ID,
        lineItem.PP_CONFIGURATOR_MOVEMENT_TIMING_LABEL,
        required,
        readOnly,
        api.local.exclusionData?.MovementTiming as Object,
        options
)

return entry