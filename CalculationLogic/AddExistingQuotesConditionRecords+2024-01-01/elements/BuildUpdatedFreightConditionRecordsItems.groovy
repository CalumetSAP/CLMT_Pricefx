import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

def existingFreight = api.local.existingFreight

def conditionRecordSetMap = out.LoadConditionRecordSetMap
def priceConditionTypes = ["ZFDL", "ZFDD"]

def keys1, keys2, keys3, keys4, keys5, keys6, groupByKeys
def minValidFrom, maxValidTo

def newItemsA904 = api.local.newFreightItemsA904
if (newItemsA904) {
    keys1 = []
    keys2 = newItemsA904.key2 ?: []
    keys3 = newItemsA904.key3 ?: []
    keys4 = newItemsA904.key4 ?: []
    keys5 = newItemsA904.key5 ?: []
    minValidFrom = (newItemsA904.validFrom ?: []).min()
    maxValidTo = (newItemsA904.validTo ?: []).max()

    groupByKeys = ["key2", "key3", "key4", "key5"]
    def existingCRs = getExtistingCRs("CRCI5", conditionRecordSetMap["A904"], priceConditionTypes, minValidFrom, maxValidTo, groupByKeys,
            keys1, keys2, keys3, keys4, keys5)

    def itemsToUpdateAndDelete = getItemsToUpdateAndDelete(newItemsA904, existingCRs, groupByKeys, existingFreight, sdf)
    api.local.freightItemsToUpdateA904 = itemsToUpdateAndDelete.itemsToUpdate
    api.local.supersededFreightItemsA904 = itemsToUpdateAndDelete.supersededItems
    def delayedItems = itemsToUpdateAndDelete.delayedItems
    api.local.freightDelayedItemsA904 = delayedItems
    api.local.newFreightItemsA904.removeAll(delayedItems)
}


def getItemsToUpdateAndDelete (newItems, existingCRs, groupByKeys, existingData, sdf) {
    def itemsToUpdate = []
    def supersededItems = []
    def delayedItems = []
    def newItemValidFrom, possibleUpdatableItems, oldItemValidFrom, oldItemValidTo, key
    def possibleItemToUpdate = null
    def supersededItem = null
    for (newItem in newItems) {
        newItemValidFrom = sdf.parse(newItem.validFrom)

        key = buildKey(newItem, groupByKeys)
        possibleUpdatableItems = existingCRs.get(key)
        supersededItem = null

        if (possibleUpdatableItems) {
            for (possibleUpdatableItem in possibleUpdatableItems) {
                oldItemValidFrom = sdf.parse(possibleUpdatableItem.validFrom as String)
                oldItemValidTo = sdf.parse(possibleUpdatableItem.validTo as String)
                supersededItem = null

                if (newItemValidFrom <= oldItemValidFrom) { //Scenario 1
                    possibleItemToUpdate = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, "Delete", "X")

                } else if (oldItemValidFrom < newItemValidFrom && oldItemValidTo >= newItemValidFrom) { //Scenario 2
                    supersededItem = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, null, "X", 1)
                    possibleItemToUpdate = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, sdf.format(newItemValidFrom-1), "Change")
                }
                if (supersededItem) supersededItems.add(supersededItem)

                if (possibleItemToUpdate && haveDifferences(newItem, possibleItemToUpdate, groupByKeys)) {
                    itemsToUpdate.add(possibleItemToUpdate)
                    if (possibleItemToUpdate.attribute4 == "Delete") delayedItems.add(newItem)
                }
            }
        } else {
            possibleItemToUpdate = generateExistingConditionRecord(existingData, newItem, newItemValidFrom, sdf)

            if (possibleItemToUpdate && haveDifferences(newItem, possibleItemToUpdate, groupByKeys)) {
                itemsToUpdate.add(possibleItemToUpdate)
                if (possibleItemToUpdate.attribute4 == "Delete") delayedItems.add(newItem)
            }
        }
    }
    return [
            itemsToUpdate   : itemsToUpdate,
            supersededItems : supersededItems,
            delayedItems    : delayedItems
    ]
}

def getExtistingCRs(String typeCode, conditionRecordSetId, priceConditionTypes, minValidFrom, maxValidTo, List<String> groupByKeys,
                    keys1 = null, keys2 = null, keys3 = null, keys4 = null, keys5 = null, keys6 = null) {
    List filters = [
            Filter.equal("conditionRecordSetId", conditionRecordSetId),
            Filter.greaterOrEqual("validTo", minValidFrom.toString()),
            Filter.lessOrEqual("validFrom", maxValidTo.toString()),
            Filter.in("key1", priceConditionTypes),
            Filter.or(
                    Filter.equal("attribute4", "Change"),
                    Filter.isNull("attribute4")
            ),
            Filter.isNull("attribute5")
    ]

    if (keys1) filters.add(Filter.in("key1", keys1))
    if (keys2) filters.add(Filter.in("key2", keys2))
    if (keys3) filters.add(Filter.in("key3", keys3))
    if (keys4) filters.add(Filter.in("key4", keys4))
    if (keys5) filters.add(Filter.in("key5", keys5))
    if (keys6) filters.add(Filter.in("key6", keys6))

    return api.stream(typeCode, null, *filters)?.withCloseable { stream ->
        stream.collect().groupBy { item ->
            groupByKeys.collect { key -> item[key] }
        }
    } ?: [:]
}

def buildKey(newItem, List<String> groupByKeys) {
    groupByKeys.collect { key -> newItem[key] }
}

def buildUpdatedCRRow (oldCRRow, newValidFrom, newValidTo, integrationFlag, supersededFlag = null, integrationStatus = 0) {
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

    def inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    def outputFormat = new SimpleDateFormat("yyyy-MM-dd")

    def validFromOutput = newValidFrom?.toString()
    if (isValidDateFormat(validFromOutput)) {
        def validFromDate = inputFormat.parse(newValidFrom)
        validFromOutput = outputFormat.format(validFromDate)
    }

    def validToOutput = newValidTo?.toString()
    if (isValidDateFormat(validToOutput)) {
        def validToDate = inputFormat.parse(newValidTo)
        validToOutput = outputFormat.format(validToDate)
    }

    updatedRow.validFrom = validFromOutput
    updatedRow.validTo = validToOutput
    updatedRow.attribute4 = integrationFlag
    updatedRow.integrationStatus = integrationStatus
    updatedRow.attribute5 = supersededFlag ?: null

    return updatedRow
}

def getExistingKey(newItem) {
    return newItem.key1 + "|" + newItem.key2 + "|" + newItem.key3 + "|" + newItem.key4 + "|" + newItem.key5 + "|" + newItem.validFrom + "|" + newItem.validTo
}

def generateExistingConditionRecord(existingData, newItem, newItemValidFrom, sdf) {
    def existingKey = getExistingKey(newItem)
    if (!existingKey) return null

    def data = existingData.get(existingKey)
    if (!data) return null

    def freightTerm = data?.get("FreightTerm")
    def conditionType = freightTerm == "3" || freightTerm == "4" ? "ZFDL" : (freightTerm == "1" || freightTerm == "2" ? "ZFDD" : "")
    def salesOrg = data?.get("SalesOrg")
    def contract = data?.get("SAPContractNumber")
    def contractItem = data?.get("SAPLineId")
    def material = data?.get("Material")

    if (!conditionType || !salesOrg || !contract || !contractItem || !material) return null

    def itemToAdd = [:]
    itemToAdd.key1 = conditionType
    itemToAdd.key2 = salesOrg
    itemToAdd.key3 = contract
    itemToAdd.key4 = contractItem
    itemToAdd.key5 = material
    def conditionRecordSetMap = out.LoadConditionRecordSetMap
    return updateConditionRecordItemForCustomEvent(itemToAdd, data, newItem.attribute1, null, null, newItemValidFrom, conditionRecordSetMap["A904"], sdf)
}

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, data, quoteId, itemScales, scaleUOM, newItemValidFrom, conditionRecordSetId, sdf) {
    if (!data?.get("FreightValidFrom")) return itemToAdd
    def validFrom = sdf.parse(data?.get("FreightValidFrom"))
    itemToAdd.validFrom = data?.get("FreightValidFrom")?.toString()
    itemToAdd.validTo = newItemValidFrom <= validFrom ? data?.get("FreightValidTo")?.toString() : sdf.format((newItemValidFrom-1))
    itemToAdd.unitOfMeasure = data?.get("FreightUOM")
    itemToAdd.priceUnit = 1
    itemToAdd.conditionValue = data?.get("FreightAmount")
    itemToAdd.currency = data?.get("Currency")
    itemToAdd.integrationStatus = 0
    itemToAdd.attribute4 = newItemValidFrom <= validFrom ? "Delete" : "Change"

    itemToAdd.attribute1 = quoteId
    if (itemScales) {
        itemToAdd.attribute2 = itemScales
    }
    if (scaleUOM) {
        itemToAdd.attribute3 = scaleUOM
    }
    itemToAdd.conditionRecordSetId = conditionRecordSetId

    return itemToAdd
}

boolean haveDifferences(newItem, possibleItemToUpdate, List groupByKeys) {
    if (!newItem || !possibleItemToUpdate) return false
    def keysToCompare = groupByKeys + ["key1", "validFrom", "validTo"]
    return keysToCompare.any { key -> newItem[key] != possibleItemToUpdate[key] }
}

def isValidDateFormat(String dateStr) {
    def sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    sdf.setLenient(false)

    try {
        def date = sdf.parse(dateStr)
        return true
    } catch (Exception e) {
        return false
    }
}