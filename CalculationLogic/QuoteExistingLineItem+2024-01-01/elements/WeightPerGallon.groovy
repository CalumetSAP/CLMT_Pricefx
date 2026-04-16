if (api.isInputGenerationExecution()) return

final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs

return api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemOutputsConstants.WEIGHT_PER_GALLON_ID) ?: BigDecimal.ZERO