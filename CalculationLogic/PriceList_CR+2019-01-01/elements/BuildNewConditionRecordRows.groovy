//def uuid = libs.PricelistLib.Calculations.getUUIDByPlId(plId) //TODO remove? or use it for custom event?

def plId = api.currentItem()?.id
def tablesUpdated = new HashSet()
def conditionRecordSetMap = out.LoadConditionRecordSetMap

def newItems = []
def existingData = [:]

if (api.local.isMassEdit || api.local.isPricingFormula) {
    def conditionType, scalesClosure
    if (api.local.isMassEdit) {
        conditionType = "ZCSP"
        scalesClosure = { plItem -> getScales(plItem)}
    } else { //isPricingFormula
        conditionType = "ZPFX"
        scalesClosure = { plItem -> null }
    }

    def salesOrg, material, shipTo, soldTo, division, scaleUOM, contract, contractItem, scales, distributionChannel, itemToAdd, customEventRow, existingKey
    for (plItem in api.local.plItems) {
        salesOrg = plItem["Sales Org"]
        material = plItem["sku"]
        if (!salesOrg || !material) continue //All the keys must exist

        division = plItem["Division"]
        scaleUOM = plItem["Scale UOM"]
        scales = scalesClosure(plItem)
        contract = plItem["Contract"]
        if (!contract) continue //All the keys must exist

        contractItem = plItem["Contract Item"]
        if (!contractItem) continue //All the keys must exist

        itemToAdd = [:]
        itemToAdd.key1 = conditionType
        itemToAdd.key2 = salesOrg
        itemToAdd.key3 = contract
        itemToAdd.key4 = contractItem
        itemToAdd.key5 = material
        itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, plItem, plId, scales, scaleUOM, conditionRecordSetMap["A904"])

        newItems.add(itemToAdd)

//        if (api.local.isMassEdit) {
            existingKey = conditionType + "|" + salesOrg + "|" + contract + "|" + contractItem + "|" + material + "|" + itemToAdd.validFrom + "|" + itemToAdd.validTo
            existingData?.put(existingKey, generateExistingData(plItem, api.local.isPricingFormula))
//        }

//        conditionRecordHelper.addOrUpdate(itemToAdd)

        tablesUpdated.add("A904")
    }
} else if (api.local.isListPriceZLIS) {
    def material, salesOrg, firstItem, validFrom, validTo, per, currency, distributionChannel, priceList, zlisPrice, zlisUOM, scales
    api.local.plItems?.groupBy { [it["sku"], it["Sales Org"], it["Pricelist Number"]] }?.each { key, plItems ->
        material = key[0]
        salesOrg = key[1]
        if (material && salesOrg) { //All the keys must exist
            firstItem = plItems.find() ?: [:]

            validFrom = firstItem["New Effective Date"]
            validTo = firstItem["New Expiration Date"]
            per = firstItem["Per"]
            currency = firstItem["Currency"]

            zlisPrice = firstItem["New List Price (ZLIS)"]
            if (zlisPrice != null) { //ZLIS - A901
                zlisUOM = firstItem["ZLIS UOM"]

                newItems.add([
//                        conditionRecordSetName  : "A901",
                        conditionRecordSetId    : conditionRecordSetMap["A901"],
                        key1                    : "ZLIS",
                        key2                    : salesOrg,
                        key3                    : material,
                        validFrom               : validFrom,
                        validTo                 : validTo,
                        unitOfMeasure           : zlisUOM,
                        priceUnit               : per,
                        conditionValue          : zlisPrice,
                        currency                : currency,
                        attribute1              : plId,
                        integrationStatus       : 0
                ])

                tablesUpdated.add("A901")
            }
        }
    }
} else if (api.local.isPricelistZBPL) { //ZBPL - A932
    def material, salesOrg, firstItem, distributionChannel, priceList, zbplPrice, zbplUOM, scales
    for (plItem in api.local.plItems) {
        salesOrg = plItem["Sales Org"]
        distributionChannel = plItem["Distribution Channel"]
        priceList = plItem["Pricelist Number"]
        material = plItem["sku"]
        if (salesOrg && distributionChannel && priceList && material) { //All the keys must exist
            scales = getScales(plItem)
            newItems.add([
//                    conditionRecordSetName  : "A932",
                    conditionRecordSetId    : conditionRecordSetMap["A932"],
                    key1                    : "ZBPL",
                    key2                    : salesOrg,
                    key3                    : distributionChannel,
                    key4                    : priceList,
                    key5                    : material,
                    validFrom               : plItem["New Effective Date"],
                    validTo                 : plItem["New Expiration Date"],
                    unitOfMeasure           : plItem["ZBPL UOM"],
                    priceUnit               : plItem["Per"],
                    conditionValue          : libs.SharedLib.RoundingUtils.round(plItem["New Base Price (ZBPL)"], 2),
                    currency                : plItem["Currency"],
                    attribute1              : plId,
                    attribute2              : scales ? scales : "",
                    attribute3              : scales ? plItem["Scale UOM"] : "",
                    integrationStatus       : 0
            ])

            tablesUpdated.add("A932")
        }
    }
}
if(api.isInputGenerationExecution()) return // if saving logic from navigator, prevent addConditionRecordAction throw an exception (error because of a logWarn)
//conditionRecordHelper.addConditionRecordAction().setCalculate(true) //TODO remove in a future? Is deprecated but now it is the only way to make it work

//for (tableNr in tablesUpdated) {
//    api.customEvent([
//            Process : "CondRecordGenerated",
//            TableNr : tableNr
//    ], "ConditionRecord")
//}
api.trace("newItems" , newItems)
api.local.tablesUpdated = tablesUpdated
api.local.newItems = newItems
api.local.existingData = existingData

return null

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, plItem, plId, scales, scaleUOM, conditionRecordSetName) {
    itemToAdd.validFrom = plItem["Effective Date"]
    itemToAdd.validTo = plItem["Expiration Date"]
    itemToAdd.unitOfMeasure = plItem["Pricing UOM"]
    itemToAdd.priceUnit = plItem["Per"]
    itemToAdd.conditionValue = plItem["New Price"]
    itemToAdd.currency = plItem["Currency"]
    itemToAdd.integrationStatus = 0
//    itemToAdd.integrationStatus = 3 //what is this field?

    itemToAdd.attribute1 = plId
    if (scales) {
        itemToAdd.attribute2 = scales
    }
    if (scaleUOM) {
        itemToAdd.attribute3 = scaleUOM
    }
    itemToAdd.conditionRecordSetId = conditionRecordSetName
//    itemToAdd.conditionRecordSetName = conditionRecordSetName

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

def generateExistingData(plItem, isPricingFormula) {
    return [
            "PriceType"        : isPricingFormula ? "1" : "2",
            "SalesOrg"         : plItem["Sales Org"],
            "Division"         : plItem["Division"],
            "SAPContractNumber": plItem["Contract"],
            "SAPLineId"        : plItem["Contract Item"],
            "Material"         : plItem["sku"],
            "PriceValidFrom"   : isPricingFormula ? plItem["Effective Date"] : plItem["Old Valid From"],
            "PriceValidTo"     : isPricingFormula ? plItem["Expiration Date"] : plItem["Old Valid To"],
            "PricingUOM"       : plItem["Pricing UOM"],
            "Per"              : plItem["Per"],
            "Price"            : plItem["Current Price"],
            "Currency"         : plItem["Currency"],
    ]
}