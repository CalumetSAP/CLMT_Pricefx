if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return api.global.guardrailMap?.get(api.local.lineId) ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.RECOMMENDED_PRICE_ID)