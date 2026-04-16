if (api.isInputGenerationExecution()) return null

def quote = api.currentItem()

if (quote?.get("quoteType") == "New Contract" || quote?.get("quoteType") == "NewContract") {
    return out.NewQuoteIndexedItems
} else if (quote?.get("quoteType") == "ExistingContractUpdate") {
    return out.ExistingQuoteIndexedItems
} else {
    return  []
}