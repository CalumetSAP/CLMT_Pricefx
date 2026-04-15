if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.SAP_CONTRACT_ID) ?: out.HiddenInputs?.get("SAPContractNumber")