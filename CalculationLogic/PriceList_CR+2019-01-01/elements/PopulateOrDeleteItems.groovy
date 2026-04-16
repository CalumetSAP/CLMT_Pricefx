def newItems = (api.local.newItems ?: []) + (api.local.existingRowsToExpire ?: [])
def supersededItems = api.local.supersededItems ?: []
def itemsToUpdate = api.local.itemsToUpdate ?: []
def delayedItems = (api.local.delayedItems ?: []) + (api.local.delayedItemsFromZBPL ?: [])

def typeCode = api.local.isMassEdit || api.local.isPricingFormula || api.local.isPricelistZBPL ? "CRCI5" : "CRCI3"

populateOrUpdate(newItems, supersededItems, itemsToUpdate, typeCode)
addPendingLines(delayedItems)

if(api.isInputGenerationExecution()) return // if saving logic from navigator, prevent addConditionRecordAction throw an exception (error because of a logWarn)
//conditionRecordHelper.addConditionRecordAction().setCalculate(true) //TODO remove in a future? Is deprecated but now it is the only way to make it work

if (api.local.tablesUpdated) {
    for (tableNr in api.local.tablesUpdated) {
        api.customEvent([
                Process : "CondRecordGenerated",
                TableNr : tableNr
        ], "ConditionRecord")
    }
}

return null

def populateOrUpdate (List newItems, supersededItems, List itemsToUpdate, typeCode) {
    for (supersededItem in supersededItems) {
        api.addOrUpdate(typeCode, supersededItem)
    }
    for (itemToUpdate in itemsToUpdate) {
        api.addOrUpdate(typeCode, itemToUpdate)
    }
    for (newItem in newItems) {
        newItem.validFrom = newItem.validFrom?.toString()
        newItem.validTo = newItem.validTo?.toString()
        newItem.priceUnit = newItem.priceUnit?.toString()
        newItem.conditionValue = newItem.conditionValue?.toString()
        api.addOrUpdate(typeCode, newItem)
    }
}

def addPendingLines(List delayedLines) {
    def cptName = libs.QuoteConstantsLibrary.Tables.PENDING_CONDITION_RECORDS
    def ppId = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()?.get("id")

    def table
    if (api.local.isMassEdit || api.local.isPricingFormula) table = "A904"
    if (api.local.isListPriceZLIS) table = "A901"
    if (api.local.isPricelistZBPL) table = "A932"

    delayedLines.each { line ->
        buildRowToAddOrUpdate(ppId, line, table)
    }
}

private def buildRowToAddOrUpdate(ppId, line, table) {
    def key = line.key1 + "|" + line.key2 + "|" + line.key3 + "|" + line.key4 + "|" + line.key5 + "|" + line.validFrom + "|" + line.validTo
    def data = api.jsonEncode(line)

    def attributeExtension = [
            "Status": libs.QuoteLibrary.Calculations.PENDING_STATUS,
            "Data"  : data,
            "Table" : table
    ]

    def req = [data: [
            header: ['lookupTable', 'name', 'attributeExtension'],
            data : [[ppId, key, api.jsonEncode(attributeExtension)]]
    ]]

    def body = api.jsonEncode(req)?.toString()

    api.boundCall("SystemUpdate", "/loaddata/JLTV", body, false)
}