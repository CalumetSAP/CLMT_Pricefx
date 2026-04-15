if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final roundingUtils = libs.QuoteLibrary.RoundingUtils

def numberOfDecimals = out.Inputs?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: "2"
def cost = out.Inputs?.get(lineItemConstants.COST_HIDDEN_ID) ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.COST_ID)

return roundingUtils.round(cost?.toBigDecimal(), numberOfDecimals?.toInteger())?.toString()
// if (api.isInputGenerationExecution()) return
//
//final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
//
//return api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.COST_ID) ?: out.HiddenInputs?.get("Cost")