if (api.isInputGenerationExecution()) return

def uniqueNamesToExclude = api.findLookupTableValues("PendingDivisionQuotes").collect { it.name } as List

def filters = [
        Filter.or(
                Filter.isNull("attributeExtension___Division")
        ),
        Filter.in("quoteType", "New Contract", "NewContract"),
        Filter.notIn("uniqueName", uniqueNamesToExclude)
]

return api.find("Q", 0, 200, "lastUpdateDate", *filters)?.collect {
    [
            uniqueName: it.uniqueName,
            quoteId   : it.typedId
    ]
} ?: []