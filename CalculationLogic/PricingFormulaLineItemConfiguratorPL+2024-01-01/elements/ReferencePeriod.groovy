final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = libs.BdpLib.UserInputs.createInputOptions(
        lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID,
        lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_LABEL,
        false,
        false,
        null,
        out.FindReferencePeriods as Map
)

return entry