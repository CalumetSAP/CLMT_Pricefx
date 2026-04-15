if (api.isInputGenerationExecution() || !api.local.lineItemSkus) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final tablesConstants = libs.QuoteConstantsLibrary.Tables

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def shipToOptions = headerConfigurator?.get(headerConstants.SHIP_TO_ID)
def selectedSoldTo = headerConfigurator?.get(headerConstants.SOLD_TO_ID)
def selectedDivision = headerConfigurator?.get(headerConstants.DIVISION_ID)
def selectedSalesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)

if (!shipToOptions && selectedSoldTo && selectedDivision && selectedSalesOrg) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().customerExtensionRows("PartnerKNVP")
    def extensionFilter = qapi.exprs().and(
            t1.customerId().equal(selectedSoldTo),
            t1.Division.equal(selectedDivision),
            t1.SalesOrg.equal(selectedSalesOrg),
            t1.PartnerFunction.in(["SH", "WE"])
    )

    def customerIds = qapi.source(t1, [t1.PartnerNumber], extensionFilter).stream { it.collect { it.PartnerNumber } } ?: []
    if (!customerIds) return []

    def t2 = qapi.tables().customers()
    def filter = qapi.exprs().and(
            qapi.exprs().or(
                    qapi.exprs().and(
                            t2.CustomerOrderBlock.notEqual(""),
                            t2.CustomerOrderBlock.isNotNull(),
                    ),
                    qapi.exprs().and(
                            t2.CustomerDeliveryBlock.notEqual(""),
                            t2.CustomerDeliveryBlock.isNotNull(),
                    ),
                    qapi.exprs().and(
                            t2.BillingBlock.notEqual(""),
                            t2.BillingBlock.isNotNull(),
                    ),

            ),
            t2.customerId().in(customerIds)
    )
    def fields = [t2.customerId(), t2.name(), t2.City, t2.Region]

    return qapi.source(t2, fields, filter).stream {
        it.collect {
            it.customerId + " - " + (it.name ?: "") + " - " + (it.City ?: "") + " - " + (it.Region ?: "")
        }
    } ?: [:]
}

return shipToOptions