if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final roundingUtils = libs.QuoteLibrary.RoundingUtils

return roundingUtils.round(out.Inputs?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_ID)?.toBigDecimal(), 4)?.toString()