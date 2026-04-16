if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return out.Inputs?.get(lineItemConstants.PLANT_ID)