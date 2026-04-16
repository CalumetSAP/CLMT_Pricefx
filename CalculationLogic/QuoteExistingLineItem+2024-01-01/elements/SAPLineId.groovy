if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.LINE_NUMBER_ID) ?: out.HiddenInputs?.get("SAPLineId")