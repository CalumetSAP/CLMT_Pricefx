if(api.isInputGenerationExecution()) return

def plId = api.currentItem()?.id
def conditionRecordSetMap = out.LoadConditionRecordSetMap

def newItems = []
def existingData = [:]

def freightTerm, conditionType, salesOrg, material, contract, contractItem, itemToAdd, existingKey
for (plItem in api.local.plItems) {
    freightTerm = plItem["Freight Term"]
    conditionType = freightTerm == "3" || freightTerm == "4" ? "ZFDL" : (freightTerm == "1" || freightTerm == "2" ? "ZFDD" : "")
    salesOrg = plItem["Sales Org"]
    material = plItem["sku"]
    if (!salesOrg || !material) continue //All the keys must exist

    contract = plItem["Contract #"]
    if (!contract) continue //All the keys must exist

    contractItem = plItem["Contract Line"]
    if (!contractItem) continue //All the keys must exist

    itemToAdd = [:]
    itemToAdd.key1 = conditionType
    itemToAdd.key2 = salesOrg
    itemToAdd.key3 = contract
    itemToAdd.key4 = contractItem
    itemToAdd.key5 = material
    itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, plItem, plId, conditionRecordSetMap["A904"])

    newItems.add(itemToAdd)

    existingKey = conditionType + "|" + salesOrg + "|" + contract + "|" + contractItem + "|" + material + "|" + itemToAdd.validFrom + "|" + itemToAdd.validTo
    existingData?.put(existingKey, generateExistingData(plItem, conditionType))
}

api.local.newFreightItems = newItems
api.local.existingFreight = existingData

return null

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, plItem, plId, conditionRecordSetName) {
    itemToAdd.validFrom = plItem["New Freight Valid From"]
    itemToAdd.validTo = plItem["New Freight Valid To"]
    itemToAdd.unitOfMeasure = plItem["Freight UOM"]
    itemToAdd.priceUnit = 1
    itemToAdd.conditionValue = plItem["New Freight Amount"]
    itemToAdd.currency = plItem["Currency"] ?: "USD"
    itemToAdd.integrationStatus = 0

    itemToAdd.attribute1 = plId
    itemToAdd.conditionRecordSetId = conditionRecordSetName

    return itemToAdd
}

def generateExistingData(plItem, conditionType) {
    return [
            "CondType"         : conditionType,
            "SalesOrg"         : plItem["Sales Org"],
            "SAPContractNumber": plItem["Contract #"],
            "SAPLineId"        : plItem["Contract Line"],
            "Material"         : plItem["sku"],
            "PriceValidFrom"   : plItem["Old Freight Valid From"],
            "PriceValidTo"     : plItem["Old Freight Valid To"],
            "PricingUOM"       : plItem["Freight UOM"],
            "Per"              : 1,
            "Price"            : plItem["Old Freight Amount"],
            "Currency"         : plItem["Currency"] ?: "USD",
    ]
}