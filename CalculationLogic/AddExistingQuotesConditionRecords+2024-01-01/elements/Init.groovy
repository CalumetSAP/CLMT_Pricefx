def minutesBefore = 10
Calendar calendar = Calendar.getInstance()
calendar.add(Calendar.MINUTE, minutesBefore * -1)

def filter = Filter.greaterOrEqual("lastUpdateDate", calendar.getTime())

def quoteIds = libs.QuoteLibrary.Calculations.getPendingExistingQuotesForCRIDs(filter) ?: []
if (!quoteIds) {
    api.abortCalculation()
}

final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

def quotes = []
def quoteItems = []
def quoteFreightItems = []
quoteIds?.each { quoteId ->
    def quote = api.getCalculableLineItemCollection(quoteId)
    quotes.add(quote)
    def quoteItemsToAdd = quote?.lineItems?.findAll {
        getInputByName(it?.inputs, lineItemInputsConstants.PRICE_CHANGE_FLAG_ID) ||
                getInputByName(it?.inputs, lineItemInputsConstants.PRICE_TYPE_HAS_CHANGED_ID) ||
                getInputByName(it?.inputs, lineItemInputsConstants.PT_CHANGED_FROM_INDEX_TO_CUSTOMER_ID) ||
                getInputByName(it?.inputs, lineItemInputsConstants.PT_CHANGED_FROM_CUSTOMER_TO_INDEX_ID) ||
                getInputByName(it?.inputs, lineItemInputsConstants.SCALES_HAS_CHANGED_ID) ||
                getInputByName(it?.inputs, lineItemInputsConstants.REJECTION_REASON_HAS_CHANGED_ID) ||
                getInputByName(it?.inputs, lineItemInputsConstants.REJECTION_REASON_ID)
    }?.each {
        it.quoteID = quoteId
    }
    quoteItems.addAll(quoteItemsToAdd)
    def quoteFreightItemsToAdd = quote?.lineItems?.findAll {
        getInputByName(it?.inputs, lineItemInputsConstants.PRICE_TYPE_ID) != "Contract Only" &&
                (getInputByName(it?.inputs, lineItemInputsConstants.FREIGHT_HAS_CHANGED_ID) ||
                getInputByName(it?.inputs, lineItemInputsConstants.FREIGHT_TERM_CHANGE_FLAG_ID))
    }?.each {
        it.quoteID = quoteId
    }
    quoteFreightItems.addAll(quoteFreightItemsToAdd)
}

api.local.quoteIds = quoteIds
api.local.quoteItems = quoteItems
api.local.quoteFreightItems = quoteFreightItems

api.trace("quoteIds", quoteIds)
api.trace("quoteItems", quoteItems)
api.trace("quoteFreightItems", quoteFreightItems)

return null

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}