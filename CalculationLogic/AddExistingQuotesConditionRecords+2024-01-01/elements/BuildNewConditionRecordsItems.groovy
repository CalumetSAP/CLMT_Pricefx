import java.text.SimpleDateFormat

final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

Map<String, String> priceTypeConditionType = libs.PricelistLib.Constants.PRICE_TYPE_CONDITION_TYPE

def newItemsA904 = []
def itemsToRemoveA904 = []
def existingData = [:]

def conditionRecordSetMap = out.LoadConditionRecordSetMap
def scales = api.local.scalesMap ?: [:]
def configurator, dsData, salesOrg, material, shipTo, scaleUOM, contract, contractItem, itemScales, itemToAdd,
        outputs, conditionType, priceType, scalesAttr, existingKey, isRejectionLine, lineValidFrom
def existingRecordsToDelete
for (item in api.local.quoteItems) {
    outputs = item?.outputs
    configurator = getInputByName(item?.inputs, lineItemInputsConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    dsData = getInputByName(item?.inputs, lineItemInputsConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    priceType = configurator?.get(lineItemInputsConstants.PRICE_TYPE_ID)
    conditionType = priceTypeConditionType[priceType]
    salesOrg = dsData?.get("SalesOrg")
    material = item?.get("sku")
    if (!salesOrg || !material || priceType == "4") continue //All the keys must exist

    itemScales = scales[item?.get("lineId")]

    scalesAttr = itemScales?.get("Scale")
    scaleUOM = itemScales?.get("ScaleUOM")
    shipTo = getOutputByName(outputs, "ShipTo")

    isRejectionLine = getInputByName(item?.inputs, lineItemInputsConstants.REJECTION_REASON_ID) && !dsData?.get("RejectionReason")

    contract = getOutputByName(outputs, "SAPContractNumber")
    if (!contract) continue //All the keys must exist

    contractItem = getOutputByName(outputs, "SAPLineId")
    if (!contractItem) continue //All the keys must exist

    if (!conditionType) {
        existingRecordsToDelete = getExistingRecordsToDelete("CRCI5", conditionRecordSetMap["A904"], item, salesOrg, contract, contractItem, material)
        lineValidFrom = getInputByName(item?.inputs, lineItemInputsConstants.PRICE_VALID_FROM_ID)
        if (existingRecordsToDelete) {
            existingRecordsToDelete.each {
                itemsToRemoveA904.add(buildNewRow(it, it.validFrom, it.validTo, item?.quoteID, lineValidFrom))
            }
        }
        continue
    }

    if (shipTo) {
        itemToAdd = [:]
        itemToAdd.key1 = conditionType
        itemToAdd.key2 = salesOrg
        itemToAdd.key3 = contract
        itemToAdd.key4 = contractItem
        itemToAdd.key5 = material
        itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, item, item?.quoteID, scalesAttr, scaleUOM, isRejectionLine, conditionRecordSetMap["A904"])

        newItemsA904.add(itemToAdd)

        existingKey = conditionType + "|" + salesOrg + "|" + contract + "|" + contractItem + "|" + material + "|" + itemToAdd.validFrom + "|" + itemToAdd.validTo
        existingData?.put(existingKey, dsData)
    }
}

api.local.newItemsA904 = newItemsA904
api.local.itemsToRemoveA904 = itemsToRemoveA904
api.local.existingData = existingData

return null

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, item, quoteId, itemScales, scaleUOM, isRejectionLine, conditionRecordSetId) {
    final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

    itemToAdd.validFrom = getInputByName(item?.inputs, lineItemInputsConstants.PRICE_VALID_FROM_ID)
    itemToAdd.validTo = getInputByName(item?.inputs, lineItemInputsConstants.PRICE_VALID_TO_ID)
    itemToAdd.unitOfMeasure = getInputByName(item?.inputs, lineItemInputsConstants.PRICING_UOM_ID)
    itemToAdd.priceUnit = getInputByName(item?.inputs, lineItemInputsConstants.PER_ID)
    itemToAdd.conditionValue = getInputByName(item?.inputs, lineItemInputsConstants.PRICE_ID)
    itemToAdd.currency = getInputByName(item?.inputs, lineItemInputsConstants.CURRENCY_ID)
    itemToAdd.integrationStatus = 0
    itemToAdd.attribute4 = null
    itemToAdd.attribute5 = null

    itemToAdd.attribute1 = quoteId
    itemToAdd.attribute2 = itemScales
    itemToAdd.attribute3 = scaleUOM

    if (isRejectionLine) {
        itemToAdd.attribute4 = "Delete"
    }

    itemToAdd.conditionRecordSetId = conditionRecordSetId

    return itemToAdd
}

def getExistingRecordsToDelete(String typeCode, conditionRecordSetId, item, key2, key3, key4, key5) {
    final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

    List filters = [
            Filter.equal("conditionRecordSetId", conditionRecordSetId),
            Filter.greaterOrEqual("validTo", getInputByName(item?.inputs, lineItemInputsConstants.PRICE_VALID_FROM_ID).toString()),
            Filter.or(
                    Filter.equal("attribute4", "Change"),
                    Filter.isNull("attribute4"),
                    Filter.notEqual("attribute4", "")
            ),
            Filter.or(
                    Filter.notEqual("attribute5", ""),
                    Filter.isNull("attribute5")
            ),
            Filter.equal("key2", key2),
            Filter.equal("key3", key3),
            Filter.equal("key4", key4),
            Filter.equal("key5", key5),
    ]

    return api.stream(typeCode, null, *filters)?.withCloseable { stream -> stream.collect() } ?: []
}

def buildNewRow(oldCRRow, newValidFrom, newValidTo, quoteId, lineValidFrom) {
    def updatedRow = [:]
    updatedRow.putAll(oldCRRow)
    updatedRow.remove("typedId")
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
    updatedRow.attribute1 = quoteId
    updatedRow.attribute4 = "Delete"
    updatedRow.integrationStatus = outputFormat.parse(lineValidFrom) > outputFormat.parse(validFromOutput) ? 1 : 0
    updatedRow.attribute5 = "X"

    return updatedRow
}

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}

def getOutputByName(outputs, name) {
    return outputs?.find { it.resultName == name}?.result
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