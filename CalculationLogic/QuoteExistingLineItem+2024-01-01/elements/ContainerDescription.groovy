if (api.isInputGenerationExecution()) return

final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs

return api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemOutputsConstants.CONTAINER_DESCRIPTION_ID) ?: ""