if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return out.Scales?.get(lineItemConstants.SCALES_ID)?.size()