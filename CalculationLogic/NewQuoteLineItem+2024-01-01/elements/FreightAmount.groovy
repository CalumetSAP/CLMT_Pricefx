if (api.isInputGenerationExecution() || api.local.isSoldToOnly) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final roundingUtils = libs.QuoteLibrary.RoundingUtils

def numberOfDecimals = out.Inputs?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: "2"

return roundingUtils.round(out.Inputs?.get(lineItemConstants.FREIGHT_AMOUNT_ID)?.toBigDecimal(), numberOfDecimals?.toInteger())?.toString()