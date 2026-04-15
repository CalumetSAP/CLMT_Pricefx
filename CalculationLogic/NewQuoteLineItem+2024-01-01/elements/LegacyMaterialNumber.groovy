if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return out.Inputs?.get(lineItemConstants.LEGACY_MATERIAL_NUMBER_ID)