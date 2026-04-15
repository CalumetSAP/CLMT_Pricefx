//final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem
//
//def quoteId = api.currentItem()?.typedId
//def tablesUpdated = new HashSet()
//
//def configurator, dsData, salesOrg, material, contract, contractItem, itemToAdd, outputs, conditionType, freightTerm
//for (item in api.local.quoteFreightItems) {
//    outputs = item?.outputs
//    configurator = getInputByName(item?.inputs, lineItemInputsConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
//    dsData = getInputByName(item?.inputs, lineItemInputsConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
//    salesOrg = dsData?.get("SalesOrg")
//    material = item?.get("sku")
//    contract = getOutputByName(outputs, "SAPContractNumber")
//    contractItem = getOutputByName(outputs, "SAPLineId")
//
//    if (!salesOrg || !material || !contract || !contractItem) continue //All the keys must exist
//
//    freightTerm = configurator?.get(lineItemInputsConstants.FREIGHT_TERM_ID) ?: findFreightTerm(getInputByName(item?.inputs, lineItemInputsConstants.FREIGHT_TERM_ID))
//    conditionType = freightTerm == "3" || freightTerm == "4" ? "ZFDL" : (freightTerm == "1" || freightTerm == "2" ? "ZFDD" : "")
//
//
//    itemToAdd = [:]
//    itemToAdd.key1 = conditionType
//    itemToAdd.key2 = salesOrg
//    itemToAdd.key3 = contract
//    itemToAdd.key4 = contractItem
//    itemToAdd.key5 = material
//    itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, item, configurator, quoteId, null, null, "A904")
//
//    conditionRecordHelper.addOrUpdate(itemToAdd)
//
//    tablesUpdated.add("A904")
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
//Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, item, configurator, quoteId, itemScales, scaleUOM, conditionRecordSetName) {
//    final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem
//
//    itemToAdd.validFrom = configurator?.get(lineItemInputsConstants.FREIGHT_VALID_FROM_ID)
//    itemToAdd.validTo = configurator?.get(lineItemInputsConstants.FREIGHT_VALID_TO_ID)
//    itemToAdd.unitOfMeasure = configurator?.get(lineItemInputsConstants.FREIGHT_UOM_ID)
//    itemToAdd.priceUnit = 1
//    itemToAdd.conditionValue = configurator?.get(lineItemInputsConstants.FREIGHT_AMOUNT_ID)
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
//
//def findFreightTerm(freightTerm) {
//    return freightTerm
//            ? getDropdownOptionsValues()["FreightTerm"]?.find { k, v -> v.toString().toUpperCase().startsWith(freightTerm.toUpperCase() as String) }?.key
//            : freightTerm
//}
//
//def getDropdownOptionsValues() {
//    def tablesConstants = libs.QuoteConstantsLibrary.Tables
//
//    def filters = [
//            Filter.equal("key1", "Quote"),
//            Filter.in("key2", ["FreightTerm"]),
//    ]
//    def data = api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)
//
//    data.inject([:]) { formatted, entry ->
//        String key = entry["key2"]
//        def value = [(entry["key3"]) : (entry["attribute1"])]
//        formatted[key] = formatted.containsKey(key) ? formatted[key] + value : value
//        formatted
//    } ?: [:]
//}