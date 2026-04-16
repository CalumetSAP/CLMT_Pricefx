def newItemsA904 = api.local.newItemsA904 ?: []
def supersededItemsA904 = api.local.supersededItemsA904 ?: []
def itemsToUpdateA904 = api.local.itemsToUpdateA904 ?: []
newItemsA904 = addLastModifiedByAndCreatedBy(newItemsA904)
supersededItemsA904 = addLastModifiedByAndCreatedBy(supersededItemsA904)
itemsToUpdateA904 = addLastModifiedByAndCreatedBy(itemsToUpdateA904)

populateOrUpdate(newItemsA904, supersededItemsA904, itemsToUpdateA904, "CRCI5")

def populateOrUpdate (newItems, supersededItems, itemsToUpdate, typeCode) {
    if (newItems) {
        for (supersededItem in supersededItems) {
            // Added by suganya to fix Black cat upgrade 16.1.0 issue
            api.addOrUpdate(typeCode, supersededItem)
           /* conditionRecordHelper.addOrUpdate("CRCI5",supersededItem)
            conditionRecordHelper.addOrUpdate([
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
           /* conditionRecordHelper.addOrUpdate("CRCI5",itemToUpdate)
            conditionRecordHelper.addOrUpdate([
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
        for (newItem in newItems) {
            newItem.validFrom = newItem.validFrom?.toString()
            newItem.validTo = newItem.validTo?.toString()
            // Added by suganya to fix Black cat upgrade 16.1.0 issue
            api.addOrUpdate(typeCode, newItem)
            /* conditionRecordHelper.addOrUpdate("CRCI5",newItem)
            conditionRecordHelper.addOrUpdate([
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
    }
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