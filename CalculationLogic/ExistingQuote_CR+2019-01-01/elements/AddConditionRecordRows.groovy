//final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem
//
//def scales = api.local.scalesMap ?: [:]
//
//def quoteId = api.currentItem()?.typedId
//def tablesUpdated = new HashSet()
//
//def configurator, dsData, salesOrg, material, shipTo, soldTo, division, scaleUOM, contract, contractItem, itemScales, distributionChannel, itemToAdd,
//        customEventRow, outputs, conditionType, priceType, scalesAttr
//for (item in api.local.quoteItems) {
//    outputs = item?.outputs
//    configurator = getInputByName(item?.inputs, lineItemInputsConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
//    dsData = getInputByName(item?.inputs, lineItemInputsConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
//    salesOrg = dsData?.get("SalesOrg")
//    material = item?.get("sku")
//    if (!salesOrg || !material) continue //All the keys must exist
//
//    priceType = configurator?.get(lineItemInputsConstants.PRICE_TYPE_ID)
//    if (priceType == "3") continue
//
//    conditionType = priceType == "1" ? "ZPFX" : (priceType == "2" ? "ZCSP" : "")
//    division = dsData?.get("Division")
//
//    itemScales = scales[item?.get("lineId")]
//    scalesAttr = itemScales?.get("Scale")
//    scaleUOM = itemScales?.get("ScaleUOM")
//    shipTo = getOutputByName(outputs, "ShipTo")
//
//    if (shipTo) {
//        contract = getOutputByName(outputs, "SAPContractNumber")
//        if (!contract) continue //All the keys must exist
//
//        if (division == "20") { //A904
//            contractItem = getOutputByName(outputs, "SAPLineId")
//            if (!contractItem) continue //All the keys must exist
//
//            itemToAdd = [:]
//            itemToAdd.key1 = conditionType
//            itemToAdd.key2 = salesOrg
//            itemToAdd.key3 = contract
//            itemToAdd.key4 = contractItem
//            itemToAdd.key5 = material
//            itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, item, quoteId, scalesAttr, scaleUOM, "A904")
//
//            conditionRecordHelper.addOrUpdate(itemToAdd)
//
//            tablesUpdated.add("A904")
//        }
//    }
//}
//
//conditionRecordHelper.addConditionRecordAction().setCalculate(true) //TODO remove in a future? Is deprecated but now it is the only way to make it work
//
//for (tableNr in tablesUpdated) {
//    api.customEvent([
//            Process : "CondRecordGenerated",
//            TableNr : tableNr
//    ], "ConditionRecord")
//}
//
//return null
//
//Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, item, quoteId, itemScales, scaleUOM, conditionRecordSetName) {
//    final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem
//
//    itemToAdd.validFrom = getInputByName(item?.inputs, lineItemInputsConstants.PRICE_VALID_FROM_ID)
//    itemToAdd.validTo = getInputByName(item?.inputs, lineItemInputsConstants.PRICE_VALID_TO_ID)
//    itemToAdd.unitOfMeasure = getInputByName(item?.inputs, lineItemInputsConstants.PRICING_UOM_ID)
//    itemToAdd.priceUnit = getInputByName(item?.inputs, lineItemInputsConstants.PER_ID)
//    itemToAdd.conditionValue = getInputByName(item?.inputs, lineItemInputsConstants.PRICE_ID)
//    itemToAdd.currency = getInputByName(item?.inputs, lineItemInputsConstants.CURRENCY_ID)
////    itemToAdd.integrationStatus = 3 //what is this field?
//
//    itemToAdd.attribute1 = quoteId
//    if (itemScales) {
//        itemToAdd.attribute2 = itemScales
//    }
//    if (scaleUOM) {
//        itemToAdd.attribute3 = scaleUOM
//    }
//    itemToAdd.conditionRecordSetName = conditionRecordSetName
//
//    return itemToAdd
//}
//
//def getInputByName(inputs, name) {
//    return inputs?.find { it.name == name }?.value
//}
//
//def getOutputByName(outputs, name) {
//    return outputs?.find { it.resultName == name}?.result
//}