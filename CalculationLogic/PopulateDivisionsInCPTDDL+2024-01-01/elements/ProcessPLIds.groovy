if (api.isInputGenerationExecution() || api.isDebugMode()) return

def uniqueNamesToExclude = api.findLookupTableValues("PendingDivisionQuotes").collect { it.name } as List

def filters = [
        Filter.or(
                Filter.isNull("attributeExtension___Division")
        ),
        Filter.in("quoteType", "New Contract", "NewContract"),
        Filter.notIn("uniqueName", uniqueNamesToExclude)
]

def newQuotes = api.find("Q", 0, 10, "lastUpdateDate", *filters)?.collect {
    [
            uniqueName: it.uniqueName,
            quoteType : "New",
            quoteId   : it.typedId
    ]
} ?: []

filters = [
        Filter.or(
                Filter.isNull("attributeExtension___Division")
        ),
        Filter.equal("quoteType", "ExistingContractUpdate"),
        Filter.notIn("uniqueName", uniqueNamesToExclude)
]

def existingQuotes = api.find("Q", 0, 10, "lastUpdateDate", *filters)?.collect {
    [
            uniqueName: it.uniqueName,
            quoteType : "Existing",
            quoteId   : it.typedId
    ]
} ?: []

def pendingQuotes = newQuotes + existingQuotes

pendingQuotes.eachWithIndex { quote, index ->
    dist.addOrUpdateCalcItem("batchNo-$index", quote.quoteId, [quoteType: quote.quoteType, uniqueName: quote.uniqueName])
}

