if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final lineItem = libs.QuoteConstantsLibrary.LineItem

def required = out.InputPriceProtection?.getFirstInput()?.getValue() == "3"
def readOnly = !api.local.isNotFreightGroup
def options = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
               "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
               "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
def movementDay = !api.isInputGenerationExecution() && out.FindExclusion ? out.FindExclusion?.attribute5?.toString() : null
def defaultValue = movementDay?.isNumber() ? movementDay?.toInteger() : null

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_ID,
        lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_LABEL,
        required,
        readOnly,
        options,
        defaultValue
)

return entry