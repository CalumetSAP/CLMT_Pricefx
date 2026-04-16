if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final roundingUtils = libs.QuoteLibrary.RoundingUtils

def numberOfDecimals = out.Inputs?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: "2"

return roundingUtils.round(out.Inputs?.get(lineItemConstants.PRICE_ID)?.toBigDecimal(), numberOfDecimals?.toInteger())?.toString()