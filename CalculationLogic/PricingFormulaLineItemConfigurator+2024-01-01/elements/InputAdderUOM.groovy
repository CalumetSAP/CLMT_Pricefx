final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.priceUOM && !api.isInputGenerationExecution() ? api.local.priceUOM : []
def defaultValue = api.local.attribute9 ? api.local.attribute9 as String : api.local.unitOfMeasure as String
def readOnly = api.local.readOnly ? true : false

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID,
        lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_LABEL,
        true,
        readOnly,
        options as List,
        defaultValue
)

return entry