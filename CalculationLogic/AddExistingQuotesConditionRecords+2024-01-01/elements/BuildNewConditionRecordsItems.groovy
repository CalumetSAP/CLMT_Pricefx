final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

Map<String, String> priceTypeConditionType = libs.PricelistLib.Constants.PRICE_TYPE_CONDITION_TYPE

def newItemsA904 = []
def existingData = [:]

def conditionRecordSetMap = out.LoadConditionRecordSetMap
def scales = api.local.scalesMap ?: [:]
def configurator, dsData, salesOrg, material, shipTo, soldTo, division, scaleUOM, contract, contractItem, itemScales, distributionChannel, itemToAdd,
        outputs, conditionType, priceType, scalesAttr, existingKey, isRejectionLine
for (item in api.local.quoteItems) {
    outputs = item?.outputs
    configurator = getInputByName(item?.inputs, lineItemInputsConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    dsData = getInputByName(item?.inputs, lineItemInputsConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    priceType = configurator?.get(lineItemInputsConstants.PRICE_TYPE_ID)
    conditionType = priceTypeConditionType[priceType]
    salesOrg = dsData?.get("SalesOrg")
    material = item?.get("sku")
    if (!conditionType || !salesOrg || !material) continue //All the keys must exist

    division = dsData?.get("Division")

    itemScales = scales[item?.get("lineId")]

    scalesAttr = itemScales?.get("Scale")
    scaleUOM = itemScales?.get("ScaleUOM")
    shipTo = getOutputByName(outputs, "ShipTo")

    isRejectionLine = getInputByName(item?.inputs, lineItemInputsConstants.REJECTION_REASON_ID) && !dsData?.get("RejectionReason")

    if (shipTo) {
        contract = getOutputByName(outputs, "SAPContractNumber")
        if (!contract) continue //All the keys must exist

        contractItem = getOutputByName(outputs, "SAPLineId")
        if (!contractItem) continue //All the keys must exist

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

    itemToAdd.attribute1 = quoteId
    if (itemScales) {
        itemToAdd.attribute2 = itemScales
    }
    if (scaleUOM) {
        itemToAdd.attribute3 = scaleUOM
    }

    if (isRejectionLine) {
        itemToAdd.attribute4 = "Delete"
    }

    itemToAdd.conditionRecordSetId = conditionRecordSetId

    return itemToAdd
}

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}

def getOutputByName(outputs, name) {
    return outputs?.find { it.resultName == name}?.result
}