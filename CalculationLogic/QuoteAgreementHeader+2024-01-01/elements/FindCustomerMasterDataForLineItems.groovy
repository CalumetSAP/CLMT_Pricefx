if (!quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def lineItemMap = [:]
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def shipTo = calculations.getInputValue(lnProduct, lineItemConstants.SHIP_TO_ID)
    if (shipTo) lineItemMap.put(lnProduct.lineId, shipTo.split(" - ").getAt(0).trim())
}

def customerIds = lineItemMap.values().toList()

def fields = ["customerId", "attribute4", "attribute5", "attribute7", "attribute8", "attribute12"]
def filter = Filter.in("customerId", customerIds)

def customerMasterData = api.stream("C", null, fields, filter)?.withCloseable { it.collectEntries {
    [(it.customerId): [
            Industry: it.attribute12,
            Address : it.attribute8,
            City    : it.attribute5,
            State   : it.attribute7,
            Country : it.attribute4,
    ]]
}}

lineItemMap.each { key, value ->
    def customerMasterItem = customerMasterData.get(value)
    lineItemMap.put(key, customerMasterItem)
}

return lineItemMap