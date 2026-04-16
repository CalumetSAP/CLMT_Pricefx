if (api.isInputGenerationExecution() || api.local.isSoldToOnly) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return out.Inputs?.get(lineItemConstants.FREIGHT_UOM_ID)