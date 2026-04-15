if (api.isInputGenerationExecution()) return

if (quote?.get("quoteType") != "ExistingContractUpdate") return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def customerConfigurator = quote.inputs.find { it.name == headerConstants.INPUTS_NAME }?.value

if (!customerConfigurator?.get(headerConstants.CONTRACT_NUMBER_ID)) {
    api.throwException("Contract Number input is required")
}

return null