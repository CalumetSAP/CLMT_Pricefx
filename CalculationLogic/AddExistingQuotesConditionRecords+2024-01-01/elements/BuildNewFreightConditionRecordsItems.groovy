final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

def newItemsA904 = []
def existingFreight = [:]

def conditionRecordSetMap = out.LoadConditionRecordSetMap
def configurator, dsData, salesOrg, material, contract, contractItem, itemToAdd, outputs, conditionType, freightTerm, existingKey, freightPreviousValues, freightValidFrom, freightValidTo
for (item in api.local.quoteFreightItems) {
    outputs = item?.outputs
    configurator = getInputByName(item?.inputs, lineItemInputsConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    dsData = getInputByName(item?.inputs, lineItemInputsConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    freightPreviousValues = getInputByName(item?.inputs, lineItemInputsConstants.FREIGHT_PREVIOUS_VALUES_ID) ?: [:]
    salesOrg = dsData?.get("SalesOrg")
    material = item?.get("sku")
    contract = getOutputByName(outputs, "SAPContractNumber")
    contractItem = getOutputByName(outputs, "SAPLineId")
    freightValidFrom = configurator?.get(lineItemInputsConstants.FREIGHT_VALID_FROM_ID)
    freightValidTo = configurator?.get(lineItemInputsConstants.FREIGHT_VALID_TO_ID)

    if (!salesOrg || !material || !contract || !contractItem || !freightValidFrom || !freightValidTo) continue //All the keys must exist

    freightTerm = configurator?.get(lineItemInputsConstants.FREIGHT_TERM_ID) ?: findFreightTerm(getInputByName(item?.inputs, lineItemInputsConstants.FREIGHT_TERM_ID))
    conditionType = freightTerm == "3" || freightTerm == "4" ? "ZFDL" : (freightTerm == "1" || freightTerm == "2" ? "ZFDD" : "")


    itemToAdd = [:]
    itemToAdd.key1 = conditionType
    itemToAdd.key2 = salesOrg
    itemToAdd.key3 = contract
    itemToAdd.key4 = contractItem
    itemToAdd.key5 = material
    itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, item, configurator, item?.quoteID, null, null, conditionRecordSetMap["A904"])

    newItemsA904.add(itemToAdd)

    existingKey = conditionType + "|" + salesOrg + "|" + contract + "|" + contractItem + "|" + material + "|" + itemToAdd.validFrom + "|" + itemToAdd.validTo
    freightPreviousValues?.put("FreightTerm", dsData?.get("FreightTerm"))
    freightPreviousValues?.put("SalesOrg", salesOrg)
    freightPreviousValues?.put("SAPContractNumber", contract)
    freightPreviousValues?.put("SAPLineId", contractItem)
    freightPreviousValues?.put("Material", material)
    freightPreviousValues?.put("Currency", dsData?.get("Currency"))
    existingFreight?.put(existingKey, freightPreviousValues)
}

api.local.newFreightItemsA904 = newItemsA904
api.local.existingFreight = existingFreight

return null

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, item, configurator, quoteId, itemScales, scaleUOM, conditionRecordSetId) {
    final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

    itemToAdd.validFrom = configurator?.get(lineItemInputsConstants.FREIGHT_VALID_FROM_ID)
    itemToAdd.validTo = configurator?.get(lineItemInputsConstants.FREIGHT_VALID_TO_ID)
    itemToAdd.unitOfMeasure = configurator?.get(lineItemInputsConstants.FREIGHT_UOM_ID)
    itemToAdd.priceUnit = 1
    itemToAdd.conditionValue = configurator?.get(lineItemInputsConstants.FREIGHT_AMOUNT_ID)
    itemToAdd.currency = getInputByName(item?.inputs, lineItemInputsConstants.CURRENCY_ID)
    itemToAdd.integrationStatus = 0
//    itemToAdd.integrationStatus = 3 //what is this field?
    itemToAdd.attribute4 = null
    itemToAdd.attribute5 = null

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

def findFreightTerm(freightTerm) {
    return freightTerm
            ? getDropdownOptionsValues()["FreightTerm"]?.find { k, v -> v.toString().toUpperCase().startsWith(freightTerm.toUpperCase() as String) }?.key
            : freightTerm
}

def getDropdownOptionsValues() {
    def tablesConstants = libs.QuoteConstantsLibrary.Tables

    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.in("key2", ["FreightTerm"]),
    ]
    def data = api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)

    data.inject([:]) { formatted, entry ->
        String key = entry["key2"]
        def value = [(entry["key3"]) : (entry["attribute1"])]
        formatted[key] = formatted.containsKey(key) ? formatted[key] + value : value
        formatted
    } ?: [:]
}