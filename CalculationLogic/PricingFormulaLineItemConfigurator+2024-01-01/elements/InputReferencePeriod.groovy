final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = !api.isInputGenerationExecution() && api.local.referencePeriod ? api.local.referencePeriod : [:]
def readOnly = api.local.readOnly ? true : false

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID,
        lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_LABEL,
        true,
        readOnly,
        null,
        options as Map
)

return entry