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
        // Added by suganya to fix Black cat upgrade 16.1.0 issue
      api.addOrUpdate(typeCode, supersededItem)
        /*conditionRecordHelper.addOrUpdate([
                key1             : supersededItem.key1,
                key2             : supersededItem.key2,
                key3            : supersededItem.key3,
                key4             : supersededItem.key4,
                key5             : supersededItem.key5,
                validFrom: supersededItem.validFrom,
                validTo: supersededItem.validTo,
                conditionRecordSetId: supersededItem.setId,
                conditionValue: supersededItem.conditionValue,
                currency         : supersededItem.currency,
                unitOfMeasure    : supersededItem.unitOfMeasure
        ]) */

    }
    for (itemToUpdate in itemsToUpdate) {
        // Added by suganya to fix Black cat upgrade 16.1.0 issue
        api.addOrUpdate(typeCode, itemToUpdate)
      // conditionRecordHelper.addOrUpdate("CRCI5",itemToUpdate)
      /*  conditionRecordHelper.addOrUpdate([
                key1             : itemToUpdate.key1,
                key2             : itemToUpdate.key2,
                key3            : itemToUpdate.key3,
                key4             : itemToUpdate.key4,
                key5             : itemToUpdate.key5,
                validFrom        : itemToUpdate.validFrom,
                validTo          : itemToUpdate.validTo,
                conditionRecordSetId : itemToUpdate.conditionRecordSetId,
                conditionValue   : itemToUpdate.conditionValue,
                currency         : itemToUpdate.currency,
                unitOfMeasure    : itemToUpdate.unitOfMeasure


        ]) */
    }
    for (itemToRemove in itemsToRemove) {
        // Added by suganya to fix Black cat upgrade 16.1.0 issue
       api.addOrUpdate(typeCode, itemToRemove)
      // conditionRecordHelper.addOrUpdate("CRCI5",itemToRemove)
       /* conditionRecordHelper.addOrUpdate([
                key1             : itemToRemove.key1,
                key2             : itemToRemove.key2,
                key3            : itemToRemove.key3,
                key4             : itemToRemove.key4,
                key5             : itemToRemove.key5,
                validFrom        : itemToRemove.validFrom,
                validTo          : itemToRemove.validTo,
                conditionRecordSetId : itemToRemove.conditionRecordSetId,
                conditionValue   : itemToRemove.conditionValue,
                currency         : itemToRemove.currency,
                unitOfMeasure    : itemToRemove.unitOfMeasure


        ]) */
    }
    for (newItem in newItems) {
        newItem.validFrom = newItem.validFrom?.toString()
        newItem.validTo = newItem.validTo?.toString()
        newItem.priceUnit = newItem.priceUnit?.toString()
        newItem.conditionValue = newItem.conditionValue?.toString()
        // Added by suganya to fix Black cat upgrade 16.1.0 issue
        api.addOrUpdate(typeCode, newItem)
       // conditionRecordHelper.addOrUpdate("CRCI5",newItem)
       /* conditionRecordHelper.addOrUpdate([
                key1             : newItem.key1,
                key2             : newItem.key2,
                key3            : newItem.key3,
                key4             : newItem.key4,
                key5             : newItem.key5,
                validFrom        : newItem.validFrom,
                validTo          : newItem.validTo,
                conditionRecordSetId : newItem.conditionRecordSetId,
                conditionValue   : newItem.conditionValue,
                currency         : newItem.currency,
                unitOfMeasure    : newItem.unitOfMeasure


        ]) */
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