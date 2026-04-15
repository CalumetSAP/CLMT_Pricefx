def newItemsA904 = (api.local.newItemsA904 ?: []) + (api.local.newFreightItemsA904 ?: [])
def supersededItemsA904 = (api.local.supersededItemsA904 ?: []) + (api.local.supersededFreightItemsA904 ?: [])
def itemsToUpdateA904 = (api.local.itemsToUpdateA904 ?: []) + (api.local.freightItemsToUpdateA904 ?: [])
def delayedItemsA904 = (api.local.delayedItemsA904 ?: []) + (api.local.freightDelayedItemsA904 ?: [])
def itemsToRemoveA904 = (api.local.itemsToRemoveA904 ?: [])

newItemsA904 = addLastModifiedByAndCreatedBy(newItemsA904)
supersededItemsA904 = addLastModifiedByAndCreatedBy(supersededItemsA904)
itemsToUpdateA904 = addLastModifiedByAndCreatedBy(itemsToUpdateA904)
delayedItemsA904 = addLastModifiedByAndCreatedBy(delayedItemsA904)
itemsToRemoveA904 = addLastModifiedByAndCreatedBy(itemsToRemoveA904)

if (api.isDebugMode()) {
    api.trace("newItemsA904", newItemsA904)
    api.trace("supersededItemsA904", supersededItemsA904)
    api.trace("itemsToUpdateA904", itemsToUpdateA904)
    api.trace("delayedItemsA904", delayedItemsA904)
    api.trace("itemsToRemoveA904", itemsToRemoveA904)
}

populateOrUpdate(newItemsA904, itemsToRemoveA904, supersededItemsA904, itemsToUpdateA904, "CRCI5")
addPendingLines(delayedItemsA904)

def populateOrUpdate (newItems, itemsToRemove, supersededItems, itemsToUpdate, typeCode) {
    for (supersededItem in supersededItems) {
        api.addOrUpdate(typeCode, supersededItem)
    }
    for (itemToUpdate in itemsToUpdate) {
        api.addOrUpdate(typeCode, itemToUpdate)
    }
    for (itemToRemove in itemsToRemove) {
        api.addOrUpdate(typeCode, itemToRemove)
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
    delayedLines.each { line ->
        buildRowToAddOrUpdate(ppId, line)
    }
}

private def buildRowToAddOrUpdate(ppId, line) {
    def key = line.key1 + "|" + line.key2 + "|" + line.key3 + "|" + line.key4 + "|" + line.key5 + "|" + line.validFrom + "|" + line.validTo
    def data = api.jsonEncode(line)

    def attributeExtension = [
            "Status": libs.QuoteLibrary.Calculations.PENDING_STATUS,
            "Data"  : data
    ]

    def req = [data: [
            header: ['lookupTable', 'name', 'attributeExtension'],
            data : [[ppId, key, api.jsonEncode(attributeExtension)]]
    ]]

    def body = api.jsonEncode(req)?.toString()

    api.boundCall("SystemUpdate", "/loaddata/JLTV", body, false)
}

// This is to fix a bug since 16.1 upgrade
private List addLastModifiedByAndCreatedBy(List lines) {
    if (!lines) return lines
    lines?.collect {
        it["lastModifiedByToken"] = "testToken"
        it["createdByToken"] = "testToken"
        return it
    }
}