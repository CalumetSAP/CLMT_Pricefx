if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.PH4_ID) ?: ""