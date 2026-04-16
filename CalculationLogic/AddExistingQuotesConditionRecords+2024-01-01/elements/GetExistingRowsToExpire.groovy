final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

Map<String, String> priceTypeConditionType = libs.PricelistLib.Constants.PRICE_TYPE_CONDITION_TYPE

def quoteIds = api.local.quoteIds
def quotes = []
def quoteItemsA904 = []

def dsData, salesOrg, material, contract, contractItem, itemToAdd, outputs, previousConditionType
quoteIds?.each { quoteId ->
    def quote = api.getCalculableLineItemCollection(quoteId)
    quotes.add(quote)
    def quoteItemsToAdd = quote?.lineItems?.findAll {
        getInputByName(it?.inputs, lineItemInputsConstants.LINE_HAS_CHANGED_ID)
    }?.each {
        it.quoteID = quoteId
    }

    for (item in quoteItemsToAdd) {
        outputs = item?.outputs
        dsData = getInputByName(item?.inputs, lineItemInputsConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
        previousConditionType = priceTypeConditionType[dsData?.get("PriceType")]
        salesOrg = dsData?.get("SalesOrg")
        material = item?.get("sku")
        if (!previousConditionType || !salesOrg || !material) continue //All the keys must exist

        contract = getOutputByName(outputs, "SAPContractNumber")
        if (!contract) continue //All the keys must exist

        contractItem = getOutputByName(outputs, "SAPLineId")
        if (!contractItem) continue //All the keys must exist

        itemToAdd = [:]
        itemToAdd.key1 = previousConditionType
        itemToAdd.key2 = salesOrg
        itemToAdd.key3 = contract
        itemToAdd.key4 = contractItem
        itemToAdd.key5 = material
        itemToAdd = updateConditionRecordItemForCustomEvent(itemToAdd, item)

        quoteItemsA904.add(itemToAdd)
    }

}

api.local.existingRowsToExpireA904 = quoteItemsA904

return null

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}

def getOutputByName(outputs, name) {
    return outputs?.find { it.resultName == name}?.result
}

Map<String, Object> updateConditionRecordItemForCustomEvent(itemToAdd, item) {
    final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

    itemToAdd.validFrom = getInputByName(item?.inputs, lineItemInputsConstants.PRICE_VALID_FROM_ID)
    itemToAdd.validTo = getInputByName(item?.inputs, lineItemInputsConstants.PRICE_VALID_TO_ID)

    return itemToAdd
}