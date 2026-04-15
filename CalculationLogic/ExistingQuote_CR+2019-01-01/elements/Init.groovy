final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

def quoteId = api.currentItem()?.typedId
def quote = api.getCalculableLineItemCollection(quoteId)

if (quote?.get("quoteType") != "ExistingContractUpdate") return

api.local.quote = quote
api.local.quoteItems = quote?.lineItems?.findAll {
    getInputByName(it?.inputs, lineItemInputsConstants.PRICE_CHANGE_FLAG_ID) ||
    getInputByName(it?.inputs, lineItemInputsConstants.PT_CHANGED_FROM_INDEX_TO_CUSTOMER_ID) ||
    getInputByName(it?.inputs, lineItemInputsConstants.PT_CHANGED_FROM_CUSTOMER_TO_INDEX_ID) ||
    getInputByName(it?.inputs, lineItemInputsConstants.SCALES_HAS_CHANGED_ID)
}
api.local.quoteFreightItems = quote?.lineItems?.findAll {
    getInputByName(it?.inputs, lineItemInputsConstants.FREIGHT_ESTIMATE_ID)
}

return null

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}