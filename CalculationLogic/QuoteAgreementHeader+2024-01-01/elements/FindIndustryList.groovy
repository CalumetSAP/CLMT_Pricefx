if (!quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedSoldTo = customerConfigurator?.get(headerConstants.SOLD_TO_ID) ?: []
//def selectedShipTos = customerConfigurator?.get(headerConstants.SHIP_TO_ID) ?: []
//if (!selectedShipTos) {
//    for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
//        if (lnProduct.folder) continue
//
//        def shipTo = calculations.getInputValue(lnProduct, lineItemConstants.SHIP_TO_ID)
//        if (shipTo) selectedShipTos.add(shipTo)
//    }
//}

//selectedShipTos = selectedShipTos?.collect { it.split(" - ").getAt(0).trim()}

def fields = ["attribute12"]
def filter = Filter.in("customerId", [selectedSoldTo])// + selectedShipTos)

def industryList = api.stream("C", null, fields, filter)
        ?.withCloseable { it.collect { it.attribute12 } }
        ?.unique()

return industryList