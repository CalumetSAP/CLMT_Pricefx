if (api.isInputGenerationExecution()) return

def quotes = out.FindQuotes

def division
def quotesList = []
quotes.each { quote ->
    division = getDivision(quote.quoteId as String, libs.QuoteConstantsLibrary.HeaderConfigurator.INPUTS_NAME, libs.QuoteConstantsLibrary.HeaderConfigurator.DIVISION_ID)

    quotesList.add(
            [
                    uniqueName: quote.uniqueName,
                    Division  : division
            ]
    )
}

addOrUpdateStatusToPending(quotesList)

return null

def addOrUpdateStatusToPending(List quotes) {
    def cptData = api.findLookupTable("PendingDivisionQuotes")
    quotes.each { quote ->
        setPendingStatus(cptData, quote.uniqueName, quote.Division)
    }
}

private def setPendingStatus(cptData, quoteId, division) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, division,"PENDING"))
}

private def buildRowToAddOrUpdate (cptData, quoteId, division, status) {
    def ppRow = [
            "lookupTableId"     : cptData.id,
            "lookupTableName"   : cptData.uniqueName,
            "name"              : quoteId,
            "attribute1"        : division,
            "attribute2"        : status,
    ]

    return ppRow
}

String getDivision(String quoteTypedId, String configuratorName, String inputName) {
    return api.getCalculableLineItemCollection(quoteTypedId)?.inputs?.
            find { inp -> inp.name == configuratorName
            }?.value?.get(inputName)
}