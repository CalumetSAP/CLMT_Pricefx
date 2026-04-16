def itemsA904 = []

def conditionRecordSetMap = out.LoadConditionRecordSetMap
def salesOrg, material, contract, contractItem, itemToAdd, conditionType, freightTerm, quoteId
for (quoteRow in out.LoadQuotesDSRows) {
    if (!quoteRow.FreightEstimate && !quoteRow.FreightAmount) continue

    salesOrg = quoteRow.SalesOrg
    material = quoteRow.Material
    if (!salesOrg || !material) continue //All the keys must exist

    contract = quoteRow.SAPContractNumber
    contractItem = quoteRow.SAPLineID
    if (!salesOrg || !material || !contract || !contractItem) continue //All the keys must exist

    freightTerm = quoteRow.FreightTerm
    conditionType = freightTerm == "3" || freightTerm == "4" ? "ZFDL" : (freightTerm == "1" || freightTerm == "2" ? "ZFDD" : "")
    if (!conditionType) continue

    quoteId = quoteRow.QuoteID

    itemToAdd = [:]
    itemToAdd.key1 = conditionType
    itemToAdd.key2 = salesOrg
    itemToAdd.key3 = contract
    itemToAdd.key4 = contractItem
    itemToAdd.key5 = material
    itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, quoteRow, quoteId, null, null, conditionRecordSetMap["A904"])

    itemsA904.add(itemToAdd)
}
api.trace(itemsA904)
api.local.newItemsA904.addAll(itemsA904)

return null

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, quoteRow, quoteId, itemScales, scaleUOM, conditionRecordSetId) {
    itemToAdd.validFrom = quoteRow.FreightValidFrom
    itemToAdd.validTo = quoteRow.FreightValidto
    itemToAdd.unitOfMeasure = quoteRow.FreightUOM
    itemToAdd.priceUnit = 1
    itemToAdd.conditionValue = quoteRow.FreightAmount
    itemToAdd.currency = quoteRow.Currency
    itemToAdd.integrationStatus = 0

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

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}

def getOutputByName(outputs, name) {
    return outputs?.find { it.resultName == name}?.result
}