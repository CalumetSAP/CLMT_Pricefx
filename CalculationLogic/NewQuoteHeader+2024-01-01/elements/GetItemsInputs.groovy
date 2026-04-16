if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def salesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)

def lineItemPlants = []
def lineItemShipTos = []
def basePricingFilters = []

def materials = []
def salesOrgs = []
def pricelists = []

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)

    if (configurator?.get(lineItemConstants.PRICE_LIST_ID)) {
        def pricelist = configurator.get(lineItemConstants.PRICE_LIST_ID)?.split(" - ")?.getAt(0)
        def commonFilter = Filter.and(
//                Filter.lessOrEqual("ValidFrom", configurator?.get(lineItemConstants.PRICE_VALID_FROM_ID)),
//                Filter.greaterOrEqual("ValidTo", configurator?.get(lineItemConstants.PRICE_VALID_FROM_ID)),
                Filter.equal("Material", lnProduct.sku)
        )
        basePricingFilters.add(Filter.and(
                commonFilter,
                Filter.equal("Pricelist", pricelist)
        ))

        //CR filters
        if (lnProduct.sku) materials.add(lnProduct.sku)
        if (salesOrg) salesOrgs.add(salesOrg)
        if (pricelist) pricelists.add(pricelist)
    }
    if (configurator?.get(lineItemConstants.PLANT_ID)) lineItemPlants.add(configurator.get(lineItemConstants.PLANT_ID))
    if (configurator?.get(lineItemConstants.SHIP_TO_ID)) lineItemShipTos.add(configurator.get(lineItemConstants.SHIP_TO_ID)?.split(" - ")?.getAt(0))
}

if (out.FindCustomerShipTo?.size() == 1) lineItemShipTos = [out.FindCustomerShipTo?.find()?.split(" - ")?.getAt(0)]

def qapi = api.queryApi()
def t1 = qapi.tables().conditionRecords("A932")

def basePricingCRFilters
if (salesOrgs && pricelists && materials) {
    basePricingCRFilters = qapi.exprs().and(
            t1.key2().in(salesOrgs?.toSet()?.findAll()?.toList()),
            t1.key4().in(pricelists?.toSet()?.findAll()?.toList()),
            t1.key5().in(materials?.toSet()?.findAll()?.toList()),
    )
}

api.local.lineItemPlants = lineItemPlants
api.local.lineItemShipTos = lineItemShipTos
api.local.basePricingFilters = basePricingFilters
api.local.basePricingCRFilters = basePricingCRFilters

return null