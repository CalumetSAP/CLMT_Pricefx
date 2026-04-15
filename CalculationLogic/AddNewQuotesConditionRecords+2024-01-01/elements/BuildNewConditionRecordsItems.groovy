Map<String, String> priceTypeConditionType = libs.PricelistLib.Constants.PRICE_TYPE_CONDITION_TYPE

def conditionRecordSetMap = out.LoadConditionRecordSetMap
def scales = api.local.scalesMap ?: [:]

def newItemsA904 = []

def quoteId, salesOrg, material, soldTo, division, scaleUOM, distributionChannel, contract, contractItem, itemToAdd, itemScales, scalesAttr, conditionType
for (quoteRow in out.LoadQuotesDSRows) {
    conditionType = priceTypeConditionType[quoteRow.PriceType]
    salesOrg = quoteRow.SalesOrg
    material = quoteRow.Material
    if (!conditionType || !salesOrg || !material) continue //All the keys must exist

    itemScales = scales[quoteRow?.get("LineID")]
    scalesAttr = itemScales?.get("Scale")
    scaleUOM = itemScales?.get("ScaleUOM")
    quoteId = quoteRow.QuoteID
    division = quoteRow.Division

    contract = quoteRow.SAPContractNumber
    if (!contract) continue //All the keys must exist

    contractItem = quoteRow.SAPLineID
    if (!contractItem) continue //All the keys must exist
    itemToAdd = [:]
    itemToAdd.key1 = conditionType
    itemToAdd.key2 = salesOrg
    itemToAdd.key3 = contract
    itemToAdd.key4 = contractItem
    itemToAdd.key5 = material
    itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, quoteRow, quoteId, scalesAttr, scaleUOM, conditionRecordSetMap["A904"])

    newItemsA904.add(itemToAdd)
}

api.local.newItemsA904 = newItemsA904

return null

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, quoteRow, quoteId, itemScales, scaleUOM, conditionRecordSetId) {
    itemToAdd.validFrom = quoteRow.PriceValidFrom
    itemToAdd.validTo = quoteRow.PriceValidTo
    itemToAdd.unitOfMeasure = quoteRow.PricingUOM
    itemToAdd.priceUnit = quoteRow.Per
    itemToAdd.conditionValue = quoteRow.Price
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