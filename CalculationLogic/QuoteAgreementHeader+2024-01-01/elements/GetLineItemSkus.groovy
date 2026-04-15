if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = customerConfigurator?.get(headerConstants.DIVISION_ID)
api.global.selectedDivision = division

def lineItemSkus = []
def lineItemShipTos = []
def lineItemPlants = []
def modeOfTransportations = []
def skusWithPL = []
def pricelists = []

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    lineItemSkus.add(lnProduct.sku)
    lineItemShipTos.add(calculations.getInputValue(lnProduct, lineItemConstants.SHIP_TO_ID))
    lineItemPlants.add(calculations.getInputValue(lnProduct, lineItemConstants.PLANT_ID))
    modeOfTransportations.add(calculations.getInputValue(lnProduct, lineItemConstants.MODE_OF_TRANSPORTATION_ID)?.split(" - ")?.getAt(0)?.trim())
    if (calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID) == "3") {
        skusWithPL.add(lnProduct.sku)
        pricelists.add(calculations.getInputValue(lnProduct, lineItemConstants.PRICE_LIST_ID))
    }
}

api.local.lineItemSkus = lineItemSkus
api.local.lineItemShipTos = lineItemShipTos
api.local.lineItemPlants = lineItemPlants?.unique()?.findAll { it }
api.local.modeOfTransportations = modeOfTransportations
api.local.skusWithPL = skusWithPL
api.local.pricelists = pricelists

return null