if (api.isInputGenerationExecution()) return null

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def quote = api.currentItem()

def showIndexColumn = false
if (quote?.get("quoteType") == "New Contract" || quote?.get("quoteType") == "NewContract") {
    for (line in quote?.lineItems) {
        def configurator = line.get("inputs").find { it.name == "Inputs" }?.value ?: [:]
        def priceType = configurator.PriceTypeInput

        if (priceType == "1") {
            showIndexColumn = true
            break
        }
    }
} else if (quote?.get("quoteType") == "ExistingContractUpdate") {
    for (line in quote?.lineItems) {
        def priceType = calculations.getInputValue(line, lineItemConstants.PRICE_TYPE_ID)
        priceType = priceType ? getPriceTypeValues()?.get(priceType) : priceType

        if (priceType == "1") {
            showIndexColumn = true
            break
        }
    }
}

return showIndexColumn

def getPriceTypeValues() {
    def tablesConstants = libs.QuoteConstantsLibrary.Tables

    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.in("key2", ["PriceType"]),
    ]
    return api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)?.collectEntries {
        [(it.attribute1) : it.key3]
    }
}