if(api.isInputGenerationExecution()) return

def changedLines = api.local.plItems

addChangedLines(changedLines)

return null

def addChangedLines(changedLines) {
    def cptName = libs.QuoteConstantsLibrary.Tables.PRICE_CHANGES_FROM_PL
    def ppId = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()?.get("id")
    def today = new Date()
    changedLines?.each { line ->
        buildRowToAddOrUpdate(ppId, line, today)
    }
}

private def buildRowToAddOrUpdate(ppId, line, today) {
    def req = [data: [
            key1      : line["sku"],
            key2      : "*",
            key3      : line["Contract #"],
            key4      : line["Contract Line"],
            key5      : line["New Price Valid From"],
            attribute1: false,
            attribute2: today
    ]]

    def body = api.jsonEncode(req)?.toString()

    def res = api.boundCall("SystemUpdate", "/lookuptablemanager.integrate/" + ppId, body, false)
}