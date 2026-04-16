if (api.isInputGenerationExecution()) return

def quotes = out.FindQuotes

def divisionMap = out.FindDivision

def completeDivision
quotes.each { quote ->
    completeDivision = quote?.Division ? (divisionMap?.get(quote?.Division) ?: "-") : "-"
    api.update("Q", [
            "uniqueName"     : quote?.key1,
            "attributeExtension___Division": completeDivision
    ])
}

addOrUpdateStatusToReady(quotes.collect { it.key1 })

return null

def addOrUpdateStatusToReady(List quoteIds) {
    def cptData = api.findLookupTable("PendingDivisionQuotes")
    quoteIds.each { quoteId ->
        setReadyStatus(cptData, quoteId)
    }
}

private def setReadyStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, "READY"))
}

private def buildRowToAddOrUpdate (cptData, quoteId, status) {
    def ppRow = [
            "lookupTableId"     : cptData.id,
            "lookupTableName"   : cptData.uniqueName,
            "name"              : quoteId,
            "attribute2"        : status,
    ]

    return ppRow
}