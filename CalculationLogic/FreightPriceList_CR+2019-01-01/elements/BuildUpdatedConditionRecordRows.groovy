import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
SimpleDateFormat dateTimeSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

def existingData = api.local.existingData

def conditionRecordSetMap = out.LoadConditionRecordSetMap

def newItems = api.local.newItems
if (!newItems) return

def minItem, finalKey
def shouldUpdateExistingRecords = newItems
        .groupBy { it.key1 + "|" + it.key2 + "|" + it.key3 + "|" + it.key4 + "|" + it.key5 }
        .collectEntries { k, items ->
            minItem = items.min { it.validFrom }
            finalKey = k + "|" + minItem.validFrom
            [(finalKey): true]
        }

def keys3 = newItems.key3 ?: []
def keys4 = newItems.key4 ?: []
def keys5 = newItems.key5 ?: []
def minValidFrom = (newItems.validFrom ?: []).min()
def maxValidTo = (newItems.validTo ?: []).max()

def groupByKeys = ["key3", "key4", "key5"]

def typeCode = "CRCI5"
def table = "A904"
def priceConditionTypes = ["ZPFX", "ZCSP"]

def existingCRs = getExtistingCRs(typeCode, conditionRecordSetMap[table], priceConditionTypes, minValidFrom, maxValidTo, groupByKeys,
        keys3, keys4, keys5)

def itemsToUpdateAndDelete = getItemsToUpdateAndDelete(newItems, existingCRs, groupByKeys, existingData, table, sdf, dateTimeSDF, shouldUpdateExistingRecords)
api.local.itemsToUpdate = itemsToUpdateAndDelete.itemsToUpdate
api.local.supersededItems = itemsToUpdateAndDelete.supersededItems
def delayedItems = itemsToUpdateAndDelete.delayedItems
api.local.delayedItems = delayedItems
api.local.newItems.removeAll(delayedItems)

return null

def getItemsToUpdateAndDelete (newItems, existingCRs, groupByKeys, existingData, table, sdf, dateTimeSDF, shouldUpdateExistingRecords) {
    def itemsToUpdate = []
    def supersededItems = []
    def delayedItems = []
    def conditionRecordSetMap = out.LoadConditionRecordSetMap
    def newItemValidFrom, newItemValidFromMinusOne, newItemValidTo, possibleUpdatableItems, oldItemValidFrom, oldItemValidTo, key
    def possibleItemToUpdate = null
    def supersededItem = null
    def greatestValidToItem = null
    def filteredPossibleUpdatableItems
    def cmpValidTo
    def inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    def outputFormat = new SimpleDateFormat("yyyy-MM-dd")
    for (newItem in newItems) {
        if (!shouldUpdateExistingRecords?.get(newItem.key1 + "|" + newItem.key2 + "|" + newItem.key3 + "|" + newItem.key4 + "|" + newItem.key5 + "|" + newItem.validFrom)) continue

        newItemValidFrom = sdf.parse(newItem.validFrom)
        newItemValidFromMinusOne = newItemValidFrom-1
        newItemValidTo = sdf.parse(newItem.validTo)

        key = buildKey(newItem, groupByKeys)
        possibleUpdatableItems = existingCRs.get(key)

        if (possibleUpdatableItems) {
            // This is to only update the last record in case we need to extend
            filteredPossibleUpdatableItems = possibleUpdatableItems.findAll { row ->
                sdf.parse(row.validFrom as String) < newItemValidFrom && sdf.parse(row.validTo as String) < newItemValidFromMinusOne
            }

            greatestValidToItem = filteredPossibleUpdatableItems.max { a, b ->
                cmpValidTo = a.validTo <=> b.validTo
                if (cmpValidTo != 0) {
                    return cmpValidTo
                }
                return a.lastUpdateDate <=> b.lastUpdateDate
            }
            for (possibleUpdatableItem in possibleUpdatableItems) {
                oldItemValidFrom = sdf.parse(possibleUpdatableItem.validFrom as String)
                oldItemValidTo = sdf.parse(possibleUpdatableItem.validTo as String)
                possibleItemToUpdate = null
                supersededItem = null

                if (newItemValidFrom <= oldItemValidFrom && newItemValidTo >= oldItemValidFrom) { //Scenario 1
                    possibleItemToUpdate = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, "Delete", conditionRecordSetMap[table], dateTimeSDF, inputFormat, outputFormat, "X")
                } else if (oldItemValidFrom < newItemValidFrom) {
                    if (oldItemValidTo >= newItemValidFrom) { //Scenario 2
                        supersededItem = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, null, conditionRecordSetMap[table], dateTimeSDF, inputFormat, outputFormat, "X", 1)
                        possibleItemToUpdate = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, sdf.format(newItemValidFrom-1), "Change", conditionRecordSetMap[table], dateTimeSDF, inputFormat, outputFormat)
                    } else if (oldItemValidTo < newItemValidFromMinusOne && possibleUpdatableItem == greatestValidToItem) { //Scenario 3 - CAL-701
                        possibleItemToUpdate = buildUpdatedCRRow(possibleUpdatableItem, sdf.format(oldItemValidTo+1), sdf.format(newItemValidFrom-1), null, conditionRecordSetMap[table], dateTimeSDF, inputFormat, outputFormat)
                        possibleItemToUpdate.attribute1 = newItem.attribute1
                        possibleItemToUpdate.attribute31 = "Extended"
                    }

                }
                if (supersededItem) supersededItems.add(supersededItem)

                if (possibleItemToUpdate && haveDifferences(newItem, possibleItemToUpdate, groupByKeys)) {
                    itemsToUpdate.add(possibleItemToUpdate)
                    if (possibleItemToUpdate.attribute4 == "Delete") delayedItems.add(newItem)
                }
            }
        } else {
            possibleItemToUpdate = generateExistingConditionRecord(existingData, newItem, newItemValidFrom, newItemValidTo, table, sdf)

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
                    keys3 = null, keys4 = null, keys5 = null) {
    List filters = [
            Filter.equal("conditionRecordSetId", conditionRecordSetId),
//            Filter.greaterOrEqual("validTo", minValidFrom.toString()),
//            Filter.lessOrEqual("validFrom", maxValidTo.toString()),
            Filter.in("key1", priceConditionTypes),
            Filter.or(
                    Filter.equal("attribute4", "Change"),
                    Filter.isNull("attribute4")
            ),
            Filter.isNull("attribute5")
    ]

    if (keys3) filters.add(Filter.in("key3", keys3))
    if (keys4) filters.add(Filter.in("key4", keys4))
    if (keys5) filters.add(Filter.in("key5", keys5))

    return api.stream(typeCode, null, *filters)?.withCloseable { stream ->
        stream.collect().groupBy { item ->
            groupByKeys.collect { key -> item[key] }
        }
    } ?: [:]
}

def buildKey(newItem, List<String> groupByKeys) {
    groupByKeys.collect { key -> newItem[key] }
}

def buildUpdatedCRRow (oldCRRow, newValidFrom, newValidTo, integrationFlag, conditionRecordSetName, dateTimeSDF, SimpleDateFormat inputFormat, SimpleDateFormat outputFormat, supersededFlag = null, integrationStatus = 0) {
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

    def validFromOutput = newValidFrom?.toString()
    if (isValidDateFormat(validFromOutput, dateTimeSDF)) {
        def validFromDate = inputFormat.parse(newValidFrom)
        validFromOutput = outputFormat.format(validFromDate)
    }

    def validToOutput = newValidTo?.toString()
    if (isValidDateFormat(validToOutput, dateTimeSDF)) {
        def validToDate = inputFormat.parse(newValidTo)
        validToOutput = outputFormat.format(validToDate)
    }

    updatedRow.validFrom = validFromOutput
    updatedRow.validTo = validToOutput
    updatedRow.attribute4 = integrationFlag
//    updatedRow.conditionRecordSetName = conditionRecordSetName
    updatedRow.conditionRecordSetId = conditionRecordSetName
    updatedRow.integrationStatus = integrationStatus
    updatedRow.attribute5 = supersededFlag ?: null

    return updatedRow
}

boolean haveDifferences(newItem, possibleItemToUpdate, List groupByKeys) {
    if (!newItem || !possibleItemToUpdate) return false
    def keysToCompare = groupByKeys + ["validFrom", "validTo"]
    return keysToCompare.any { key -> newItem[key] != possibleItemToUpdate[key] }
}

def isValidDateFormat(String dateStr, SimpleDateFormat sdf) {
    sdf.setLenient(false)

    try {
        def date = sdf.parse(dateStr)
        return true
    } catch (Exception e) {
        return false
    }
}

def getExistingKey(newItem, table) {
    if (table == "A904") {
        return newItem.key1 + "|" + newItem.key2 + "|" + newItem.key3 + "|" + newItem.key4 + "|" + newItem.key5 + "|" + newItem.validFrom + "|" + newItem.validTo
    } else {
        return null
    }
}

def generateExistingConditionRecord(existingData, newItem, newItemValidFrom, newItemValidTo, table, sdf) {
    def existingKey = getExistingKey(newItem, table)
    if (!existingKey) return null

    def data = existingData.get(existingKey)
    if (!data) return null

    def conditionType = data?.get("CondType")
    def salesOrg = data?.get("SalesOrg")
    def contract = data?.get("SAPContractNumber")
    def contractItem = data?.get("SAPLineId")
    def material = data?.get("Material")

    def itemToAdd = [:]
    if (table == "A904") {
        if (!conditionType || !salesOrg || !contract || !contractItem || !material) return null
        itemToAdd.key1 = conditionType
        itemToAdd.key2 = salesOrg
        itemToAdd.key3 = contract
        itemToAdd.key4 = contractItem
        itemToAdd.key5 = material
    } else {
        return null
    }
    def conditionRecordSetMap = out.LoadConditionRecordSetMap
    return updateConditionRecordItemForCustomEvent(itemToAdd, data, newItem.attribute1, null, null, newItemValidFrom, newItemValidTo, conditionRecordSetMap[table], sdf)
}

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, data, quoteId, itemScales, scaleUOM, newItemValidFrom, newItemValidTo, conditionRecordSetId, sdf) {
    def validFrom = sdf.parse(data?.get("PriceValidFrom"))
    itemToAdd.validFrom = data?.get("PriceValidFrom")?.toString()
    itemToAdd.unitOfMeasure = data?.get("PricingUOM")
    itemToAdd.priceUnit = data?.get("Per")
    itemToAdd.conditionValue = data?.get("Price")
    itemToAdd.currency = data?.get("Currency")
    itemToAdd.integrationStatus = 0

    if (newItemValidFrom <= validFrom) {
        itemToAdd.validTo = data?.get("PriceValidTo")?.toString()
        if (newItemValidTo >= validFrom) {
            itemToAdd.attribute4 = "Delete"
            itemToAdd.attribute5 = "X"
        }
    } else {
        itemToAdd.validTo = sdf.format((newItemValidFrom-1))
        itemToAdd.attribute4 = "Change"
    }

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