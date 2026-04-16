if (api.isInputGenerationExecution()) return null

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def quote = api.currentItem()

def currency = null
if (quote?.get("quoteType") == "New Contract" || quote?.get("quoteType") == "NewContract") {
    for (line in quote?.lineItems) {
        if (!line.folder) {
            def configurator = line.get("inputs").find { it.name == "Inputs" }?.value ?: [:]
            currency = configurator.CurrencyInput
            if (currency == "US3" || currency == "US4") {
                currency = "USD"
            }
            break
        }
    }
} else if (quote?.get("quoteType") == "ExistingContractUpdate") {
    for (line in quote?.lineItems) {
        if (!line.folder) {
            currency = calculations.getInputValue(line, lineItemConstants.CURRENCY_ID)
            if (currency == "US3" || currency == "US4") {
                currency = "USD"
            }
            break
        }
    }
}

return currency ?: ""