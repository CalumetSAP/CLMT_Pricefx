if (api.isInputGenerationExecution() || api.local.isSoldToOnly) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return api.global.shipToData ? api.global.shipToData?.State : out.Inputs?.get(lineItemConstants.SHIP_TO_STATE_ID)