final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = libs.BdpLib.UserInputs.createInputOptions(
        lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID,
        lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_LABEL,
        false,
        false,
        null,
        out.FindIndexNumbers as List
)

return entry