final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = out.FindPlants && !api.isInputGenerationExecution() ? out.FindPlants : []
def defaultValue = options?.size() == 1 ? options?.find() : null

def entry = libs.BdpLib.UserInputs.createInputOption(
        lineItemConstants.PLANT_ID,
        lineItemConstants.PLANT_LABEL,
        true,
        false,
        options as List,
        defaultValue
)

return entry