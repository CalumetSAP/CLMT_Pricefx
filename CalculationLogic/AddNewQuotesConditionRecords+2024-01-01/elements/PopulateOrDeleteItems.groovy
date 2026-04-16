populateOrUpdate(api.local.newItemsA904, api.local.supersededItemsA904, api.local.itemsToUpdateA904, "CRCI5")

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