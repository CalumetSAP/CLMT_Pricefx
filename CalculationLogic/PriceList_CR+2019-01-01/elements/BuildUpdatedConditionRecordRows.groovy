import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
SimpleDateFormat dateTimeSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

def existingData = api.local.existingData

def conditionRecordSetMap = out.LoadConditionRecordSetMap

def keys1, keys2, keys3, keys4, keys5, groupByKeys
def minValidFrom, maxValidTo
def typeCode, table, priceConditionTypes

def newItems = api.local.newItems
if (!newItems) return
if (api.local.isMassEdit) {
    keys1 = []
    keys2 = newItems.key2 ?: []
    keys3 = newItems.key3 ?: []
    keys4 = newItems.key4 ?: []
    keys5 = newItems.key5 ?: []
    minValidFrom = (newItems.validFrom ?: []).min()
    maxValidTo = (newItems.validTo ?: []).max()

    groupByKeys = ["key2", "key3", "key4", "key5"]

    typeCode = "CRCI5"
    table = "A904"
    priceConditionTypes = ["ZCSP"]
} else if (api.local.isListPriceZLIS) {
    keys1 = []
    keys2 = newItems.key2 ?: []
    keys3 = newItems.key3 ?: []
    keys4 = []
    keys5 = []
    minValidFrom = (newItems.validFrom ?: []).min()
    maxValidTo = (newItems.validTo ?: []).max()

    groupByKeys = ["key2", "key3"]

    typeCode = "CRCI3"
    table = "A901"
    priceConditionTypes = ["ZLIS"]
} else if (api.local.isPricelistZBPL) {
    keys1 = []
    keys2 = newItems.key2 ?: []
    keys3 = newItems.key3 ?: []
    keys4 = newItems.key4 ?: []
    keys5 = newItems.key5 ?: []
    minValidFrom = (newItems.validFrom ?: []).min()
    maxValidTo = (newItems.validTo ?: []).max()

    groupByKeys = ["key2", "key3", "key4", "key5"]

    typeCode = "CRCI5"
    table = "A932"
    priceConditionTypes = ["ZBPL"]
} else if (api.local.isPricingFormula) {
    keys1 = []
    keys2 = newItems.key2 ?: []
    keys3 = newItems.key3 ?: []
    keys4 = newItems.key4 ?: []
    keys5 = newItems.key5 ?: []
    minValidFrom = (newItems.validFrom ?: []).min()
    maxValidTo = (newItems.validTo ?: []).max()

    groupByKeys = ["key2", "key3", "key4", "key5"]

    typeCode = "CRCI5"
    table = "A904"
    priceConditionTypes = ["ZPFX"]
} else {
    return
}

def existingCRs = getExtistingCRs(typeCode, conditionRecordSetMap[table], priceConditionTypes, minValidFrom, maxValidTo, groupByKeys,
        keys1, keys2, keys3, keys4, keys5)

def itemsToUpdateAndDelete = getItemsToUpdateAndDelete(newItems, existingCRs, groupByKeys, existingData, table, sdf, dateTimeSDF)
api.local.itemsToUpdate = itemsToUpdateAndDelete.itemsToUpdate
api.local.supersededItems = itemsToUpdateAndDelete.supersededItems
def delayedItems = itemsToUpdateAndDelete.delayedItems
api.local.delayedItems = delayedItems
api.local.newItems.removeAll(delayedItems)

return null

def getItemsToUpdateAndDelete (newItems, existingCRs, groupByKeys, existingData, table, sdf, dateTimeSDF) {
    def itemsToUpdate = []
    def supersededItems = []
    def delayedItems = []
    def typedIdsToDelete = new HashSet()
    def conditionRecordSetMap = out.LoadConditionRecordSetMap
    def newItemValidFrom, possibleUpdatableItems, oldItemValidFrom, oldItemValidTo, key
    def possibleItemToUpdate = null
    def supersededItem = null
    def inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    def outputFormat = new SimpleDateFormat("yyyy-MM-dd")
    for (newItem in newItems) {
        newItemValidFrom = sdf.parse(newItem.validFrom)

        key = buildKey(newItem, groupByKeys)
        possibleUpdatableItems = existingCRs.get(key)

        if (possibleUpdatableItems) {
            for (possibleUpdatableItem in possibleUpdatableItems) {
                oldItemValidFrom = sdf.parse(possibleUpdatableItem.validFrom as String)
                oldItemValidTo = sdf.parse(possibleUpdatableItem.validTo as String)
                supersededItem = null

                if (newItemValidFrom <= oldItemValidFrom) { //Scenario 1
                    possibleItemToUpdate = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, "Delete", conditionRecordSetMap[table], dateTimeSDF, inputFormat, outputFormat, "X")

                } else if (oldItemValidFrom < newItemValidFrom && oldItemValidTo >= newItemValidFrom) { //Scenario 2
                    supersededItem = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, possibleUpdatableItem.validTo, null, conditionRecordSetMap[table], dateTimeSDF, inputFormat, outputFormat, "X", 1)
                    possibleItemToUpdate = buildUpdatedCRRow(possibleUpdatableItem, possibleUpdatableItem.validFrom, sdf.format(newItemValidFrom-1), "Change", conditionRecordSetMap[table], dateTimeSDF, inputFormat, outputFormat)

                }
                if (supersededItem) supersededItems.add(supersededItem)

                if (possibleItemToUpdate && haveDifferences(newItem, possibleItemToUpdate, groupByKeys)) {
                    itemsToUpdate.add(possibleItemToUpdate)
                    if (possibleItemToUpdate.attribute4 == "Delete") delayedItems.add(newItem)
                }
            }
        } else {
            possibleItemToUpdate = generateExistingConditionRecord(existingData, newItem, newItemValidFrom, table, sdf)

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
                    keys1 = null, keys2 = null, keys3 = null, keys4 = null, keys5 = null) {
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

def generateExistingConditionRecord(existingData, newItem, newItemValidFrom, table, sdf) {
    def existingKey = getExistingKey(newItem, table)
    if (!existingKey) return null

    def data = existingData.get(existingKey)
    api.trace("data", data)
    if (!data) return null

    Map<String, String> priceTypeConditionType = libs.PricelistLib.Constants.PRICE_TYPE_CONDITION_TYPE
    def conditionType = priceTypeConditionType[data?.get("PriceType")]
    def salesOrg = data?.get("SalesOrg")
    def division = data?.get("Division")
    def contract = data?.get("SAPContractNumber")
    def contractItem = data?.get("SAPLineId")
    def material = data?.get("Material")
    def distributionChannel = "10" // TODO

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
    return updateConditionRecordItemForCustomEvent(itemToAdd, data, newItem.attribute1, null, null, newItemValidFrom, conditionRecordSetMap[table], sdf)
}

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, data, quoteId, itemScales, scaleUOM, newItemValidFrom, conditionRecordSetId, sdf) {
    def validFrom = sdf.parse(data?.get("PriceValidFrom"))
    itemToAdd.validFrom = data?.get("PriceValidFrom")?.toString()
    itemToAdd.validTo = newItemValidFrom <= validFrom ? data?.get("PriceValidTo")?.toString() : sdf.format((newItemValidFrom-1))
    itemToAdd.unitOfMeasure = data?.get("PricingUOM")
    itemToAdd.priceUnit = data?.get("Per")
    itemToAdd.conditionValue = data?.get("Price")
    itemToAdd.currency = data?.get("Currency")
    itemToAdd.integrationStatus = 0
    itemToAdd.attribute4 = newItemValidFrom <= validFrom ? "Delete" : "Change"
    itemToAdd.attribute5 = newItemValidFrom <= validFrom ? "X" : null

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