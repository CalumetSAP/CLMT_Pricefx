if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final lineItem = libs.QuoteConstantsLibrary.LineItem

def required = out.InputPriceProtection?.getFirstInput()?.getValue() == "3"
def readOnly = !api.local.isNotFreightGroup
def options = !api.isInputGenerationExecution() && api.local.movementTimingOptions ? api.local.movementTimingOptions as Map : [:]
def defaultValue = api.local.movementTimingOptions?.find { k, v -> v == out.FindExclusion?.attribute3 }?.key as Object

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItem.PP_CONFIGURATOR_MOVEMENT_TIMING_ID,
        lineItem.PP_CONFIGURATOR_MOVEMENT_TIMING_LABEL,
        required,
        readOnly,
        defaultValue,
        options
)

return entry