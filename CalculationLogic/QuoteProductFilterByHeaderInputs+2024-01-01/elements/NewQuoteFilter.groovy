if (api.isInputGenerationExecution()) return

if (quote?.get("quoteType") != "New Contract" && quote?.get("quoteType") != "NewContract") return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def customerConfigurator = quote.inputs.find { it.name == headerConstants.INPUTS_NAME }?.value
def division = customerConfigurator?.get(headerConstants.DIVISION_ID)

if (!division) {
    api.throwException("Select Division on header before adding an item")
}

return Filter.equal("attribute6", division)