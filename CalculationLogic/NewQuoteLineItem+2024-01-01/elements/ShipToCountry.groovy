if (api.isInputGenerationExecution() || api.local.isSoldToOnly) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return api.global.shipToData ? api.global.shipToData?.Country : out.Inputs?.get(lineItemConstants.SHIP_TO_COUNTRY_ID)