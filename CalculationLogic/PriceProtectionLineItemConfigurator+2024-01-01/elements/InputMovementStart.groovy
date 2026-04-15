if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final lineItem = libs.QuoteConstantsLibrary.LineItem

def required = out.InputMovementTiming?.getFirstInput()?.getValue() == "Quarter"
def readOnly = !api.local.isNotFreightGroup
def options = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
def movementStart = !api.isInputGenerationExecution() && out.FindExclusion ? out.FindExclusion?.attribute4?.toString() : null
def defaultValue = movementStart?.isNumber() ? movementStart?.toInteger() : null

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItem.PP_CONFIGURATOR_MOVEMENT_START_ID,
        lineItem.PP_CONFIGURATOR_MOVEMENT_START_LABEL,
        required,
        readOnly,
        options,
        defaultValue
)

return entry