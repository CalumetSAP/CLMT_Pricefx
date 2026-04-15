final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.indexNumber && !api.isInputGenerationExecution() ? api.local.indexNumber : []
def readOnly = api.local.readOnly ? true : false

def entry = libs.BdpLib.UserInputs.createInputOptions(
        lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID,
        lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_LABEL,
        true,
        readOnly,
        null,
        options as List
)

if(entry.getFirstInput()?.getValue()?.size() > 3) {
    entry.getFirstInput()?.setValue(entry.getFirstInput()?.getValue()?.take(3))
}

return entry