import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

def conditionRecordSetMap = out.LoadConditionRecordSetMap

def keys1 = new HashSet()
def keys2 = new HashSet()
def keys3 = new HashSet()
def keys4 = new HashSet()
def keys5 = new HashSet()
def keys6 = new HashSet()
def minValidFrom, maxValidTo

def newItemsA904 = api.local.newItemsA904
if (newItemsA904) {
    keys1.addAll(newItemsA904.key1 ?: [])
    keys2.addAll(newItemsA904.key2 ?: [])
    keys3.addAll(newItemsA904.key3 ?: [])
    keys4.addAll(newItemsA904.key4 ?: [])
    keys5.addAll(newItemsA904.key5 ?: [])
    minValidFrom = (newItemsA904.validFrom ?: []).min()
    maxValidTo = (newItemsA904.validTo ?: []).max()

    def existingCRs = getExtistingCRs("CRCI5", conditionRecordSetMap["A904"], minValidFrom, maxValidTo, keys1, keys2, keys3, keys4, keys5)

    def itemsToUpdateAndDelete = getItemsToUpdateAndDelete(newItemsA904, existingCRs, sdf)
    api.local.itemsToUpdateA904 = itemsToUpdateAndDelete.itemsToUpdate
    api.local.supersededItemsA904 = itemsToUpdateAndDelete.supersededItems
}

def getItemsToUpdateAndDelete (newItems, existingCRs, sdf) {
    def itemsToUpdate = []
    def supersededItems = []
    def supersededItem = null
    def newItemValidFrom, newItemValidTo, possibleUpdatableItems, oldItemValidFrom, oldItemValidTo
    for (newItem in newItems) {
        newItemValidFrom = newItem.validFrom
        newItemValidTo = newItem.validTo

        possibleUpdatableItems = existingCRs.get([newItem.key1, newItem.key2, newItem.key3, newItem.key4, newItem.key5, newItem.key6])

        for (possibleUpdatableItem in possibleUpdatableItems) {
            supersededItem = null
            oldItemValidFrom = sdf.parse(possibleUpdatableItem.validFrom as String)
            oldItemValidTo = sdf.parse(possibleUpdatableItem.validTo)

            if (newItemValidFrom <= oldItemValidFrom && newItemValidTo >= oldItemValidTo) { //Scenario 1
                supersededItem = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, "X")

            } else if (oldItemValidFrom < newItemValidFrom && oldItemValidTo > newItemValidTo) { //Scenario 2
                supersededItem = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, "X")
                itemsToUpdate.add(buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, newItemValidFrom-1))
                itemsToUpdate.add(buildUpdatedCRRow(possibleUpdatableItem, newItemValidTo+1, possibleUpdatableItem.validTo))

            } else if (oldItemValidFrom < newItemValidFrom && oldItemValidTo >= newItemValidFrom) { //Scenario 3
                supersededItem = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, "X")
                itemsToUpdate.add(buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, newItemValidFrom-1))

            } else if (oldItemValidFrom <= newItemValidTo && oldItemValidTo > newItemValidTo) { //Scenario 4
                supersededItem = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, "X")
                itemsToUpdate.add(buildUpdatedCRRow(possibleUpdatableItem, newItemValidTo+1, possibleUpdatableItem.validTo))
            }
            if (supersededItem) supersededItems.add(supersededItem)
        }
    }
    return [
            itemsToUpdate   : itemsToUpdate,
            supersededItems : supersededItems
    ]
}

def getExtistingCRs (String typeCode, conditionRecordSetId, minValidFrom, maxValidTo, keys1, keys2, keys3, keys4, keys5 = null, keys6 = null) {
    List filters = [
            Filter.equal("conditionRecordSetId", conditionRecordSetId),
            Filter.greaterOrEqual("validTo", minValidFrom.toString()),
            Filter.lessOrEqual("validFrom", maxValidTo.toString()),
            Filter.in("key1", keys1),
            Filter.in("key2", keys2),
            Filter.in("key3", keys3),
            Filter.in("key4", keys4),
            Filter.isNull("attribute5")
    ]
    if (keys5) {
        filters.add(Filter.in("key5", keys5))
    }
    if (keys6) {
        filters.add(Filter.in("key6", keys6))
    }

    return api.stream(typeCode, null, *filters)?.withCloseable {
        it.collect().groupBy { [it.key1, it.key2, it.key3, it.key4, it.key5, it.key6] }
    } ?: [:]
}

def buildUpdatedCRRow (oldCRRow, newValidFrom, newValidTo, supersededFlag = null) {
    def updatedRow = [:]
    updatedRow.putAll(oldCRRow)
    updatedRow.remove("createDate")
    updatedRow.remove("createdBy")
    updatedRow.remove("createdByObj")
    updatedRow.remove("id")
    updatedRow.remove("integrationStatus")
    updatedRow.remove("lastUpdateBy")
    updatedRow.remove("lastUpdateDate")
    updatedRow.remove("lastUpdatedByObj")
    updatedRow.remove("version")

    updatedRow.validFrom = newValidFrom?.toString()
    updatedRow.validTo = newValidTo?.toString()
    if (supersededFlag) {
        updatedRow.integrationStatus = 1
        updatedRow.attribute5 = supersededFlag
    } else {
        updatedRow.integrationStatus = 0
        updatedRow.attribute5 = null
    }

    return updatedRow
}