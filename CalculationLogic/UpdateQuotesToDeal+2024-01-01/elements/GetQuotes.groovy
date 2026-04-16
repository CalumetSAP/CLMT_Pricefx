if (api.isInputGenerationExecution()) return

def filters = [
        Filter.equal("quoteStatus", "OFFER"),
        Filter.or(
                Filter.equal("quoteType", "NewContract"),
                Filter.equal("quoteType", "New Contract")
        ),
        out.GetDaysFilter
]

def quotesInfo = api.find("Q", 0, api.getMaxFindResultsLimit(), "-lastUpdateDate", null, *filters)?.groupBy {
    it.typedId
}?.collectEntries { typedId, items ->
    [typedId, items?.find()?.createdByName]
}

def existingFilters = [
        Filter.equal("quoteStatus", "OFFER"),
        Filter.equal("quoteType", "ExistingContractUpdate"),
        out.GetDaysFilter
]

def existingQuotes = api.find("Q", 0, api.getMaxFindResultsLimit(), "-lastUpdateDate", null, *existingFilters)?.collect { it.typedId }

def quotes = []
quotesInfo.each { typedId, createdByName ->
    if (api.isUserInGroup("Pricing", createdByName)) {
        quotes.add(typedId)
    }
}

quotes.addAll(existingQuotes)

return quotes

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}