if (api.isInputGenerationExecution()) return

def uniqueNamesToExclude = api.findLookupTableValues("PendingDivisionQuotes").collect { it.name } as List

def filters = [
        Filter.or(
                Filter.isNull("attributeExtension___Division")
        ),
        Filter.equal("quoteType", "ExistingContractUpdate"),
        Filter.notIn("uniqueName", uniqueNamesToExclude)
]

return api.find("Q", 0, 150, "lastUpdateDate", *filters)?.collect {
    [
            uniqueName: it.uniqueName,
            quoteId   : it.typedId
    ]
} ?: []