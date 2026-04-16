if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return out.Inputs?.get(lineItemConstants.MATERIAL_MARGIN_ID)?.toBigDecimal() ?: api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.MATERIAL_MARGIN_ID)
//if (api.isInputGenerationExecution()) return
//
//final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs
//
//return api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemOutputsConstants.MATERIAL_MARGIN_ID) ?: BigDecimal.ZERO