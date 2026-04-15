if(api.isInputGenerationExecution()) return

def plId = api.currentItem()?.id
def conditionRecordSetMap = out.LoadConditionRecordSetMap

def newItems = []
def existingData = [:]

def scalesClosure = { plItem -> getScales(plItem)}

def conditionType, salesOrg, material, scaleUOM, contract, contractItem, scales, itemToAdd, existingKey
for (plItem in api.local.plItems) {
    conditionType = plItem["Cond Type"]
    salesOrg = plItem["Sales Org"]
    material = plItem["sku"]
    if (!salesOrg || !material) continue //All the keys must exist

    scaleUOM = plItem["Scale UOM"]
    scales = scalesClosure(plItem)
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
    itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, plItem, plId, scales, scaleUOM, conditionRecordSetMap["A904"])

    newItems.add(itemToAdd)

    existingKey = conditionType + "|" + salesOrg + "|" + contract + "|" + contractItem + "|" + material + "|" + itemToAdd.validFrom + "|" + itemToAdd.validTo
    existingData?.put(existingKey, generateExistingData(plItem))
}

api.local.newItems = newItems
api.local.existingData = existingData

return null

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, plItem, plId, scales, scaleUOM, conditionRecordSetName) {
    itemToAdd.validFrom = plItem["New Price Valid From"]
    itemToAdd.validTo = plItem["New Price Valid To"]
    itemToAdd.unitOfMeasure = plItem["Product UOM"]
    itemToAdd.priceUnit = 1
    itemToAdd.conditionValue = plItem["New Product Price"]
    itemToAdd.currency = plItem["Currency"] ?: "USD"
    itemToAdd.integrationStatus = 0

    itemToAdd.attribute1 = plId
    if (scales) {
        itemToAdd.attribute2 = scales
    }
    if (scaleUOM && scales) {
        itemToAdd.attribute3 = scaleUOM
    }
    itemToAdd.conditionRecordSetId = conditionRecordSetName

    return itemToAdd
}

def getScales (plItem) {
    def scaleQtyAux, priceAux
    def scales = []
    def numberOfDecimals = plItem["Number of Decimals"] ? plItem["Number of Decimals"]?.toInteger() : 2
    for (int i = 1; i < 6; i++) {
        scaleQtyAux = plItem["Scale Qty ${i}"]
        priceAux = libs.SharedLib.RoundingUtils.round(plItem["Price ${i}"], numberOfDecimals)
        if (scaleQtyAux && priceAux) {
            scales.add([
                    scaleQty: scaleQtyAux,
                    price: priceAux
            ])
        }
    }
    scales.sort { it.scaleQty }
    return scales.collect { "${it.scaleQty}=${it.price}" }.join("|")
}

def generateExistingData(plItem) {
    return [
            "CondType"         : plItem["Cond Type"],
            "SalesOrg"         : plItem["Sales Org"],
            "SAPContractNumber": plItem["Contract #"],
            "SAPLineId"        : plItem["Contract Line"],
            "Material"         : plItem["sku"],
            "PriceValidFrom"   : plItem["Old Price Valid From"],
            "PriceValidTo"     : plItem["Old Price Valid To"],
            "PricingUOM"       : plItem["Product UOM"],
            "Per"              : 1,
            "Price"            : plItem["Old Product Price"],
            "Currency"         : plItem["Currency"] ?: "USD",
    ]
}