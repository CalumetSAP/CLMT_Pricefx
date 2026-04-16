final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = libs.BdpLib.UserInputs.createInputOptions(
        lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID,
        lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_LABEL,
        false,
        false,
        null,
        out.FindRecalculationPeriods as List
)

return entry