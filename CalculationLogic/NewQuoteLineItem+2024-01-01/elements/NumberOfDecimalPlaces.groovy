if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return out.Inputs?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID)