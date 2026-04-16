if (api.isInputGenerationExecution()) return

def quotes = out.FindQuotes

def division
def quotesList = []
quotes.each { quote ->
    def quoteIdNumber = quote.quoteId.replace(".Q", "")
    def lineItem = api.find("QLI", Filter.equal("clicId", quoteIdNumber)).find()
    if (!lineItem) {
        quotesList.add(
                [
                        uniqueName: quote.uniqueName,
                        Division  : null
                ]
        )
        return
    }
    def item = api.getCalculableLineItem(quoteIdNumber + ".Q", lineItem?.lineId)

    division = getDivision(item, libs.QuoteConstantsLibrary.LineItem.DATA_SOURCE_VALUES_HIDDEN_ID, "Division")

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

String getDivision(item, String configuratorName, String inputName) {
    return item?.inputs?.
            find { inp -> inp.name == configuratorName
            }?.value?.get(inputName)
}