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
            api.addOrUpdate(typeCode, supersededItem)
        }
        for (itemToUpdate in itemsToUpdate) {
            api.addOrUpdate(typeCode, itemToUpdate)
        }
        for (newItem in newItems) {
            newItem.validFrom = newItem.validFrom?.toString()
            newItem.validTo = newItem.validTo?.toString()
            api.addOrUpdate(typeCode, newItem)
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