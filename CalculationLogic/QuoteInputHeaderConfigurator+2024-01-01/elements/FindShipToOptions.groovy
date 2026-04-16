if (api.isInputGenerationExecution() || out.InputSoldToOnlyQuote?.getFirstInput()?.getValue()) return []

def selectedSalesOrg = out.InputSalesOrg?.getFirstInput()?.getValue()

if (!selectedSalesOrg) return []

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def selectedSoldTo = out.InputSoldTo?.getFirstInput()?.getValue()
def selectedDivision = out.InputDivision?.getFirstInput()?.getValue()

def qapi = api.queryApi()
def t1 = qapi.tables().customerExtensionRows(tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION)
def filter = qapi.exprs().and(
        t1.customerId().equal(selectedSoldTo),
        t1.Division.equal(selectedDivision),
        t1.SalesOrg.equal(selectedSalesOrg),
        t1.PartnerFunction.in(["SH", "WE"])
)

def customerIds =  qapi.source(t1, [t1.PartnerNumber], filter).stream { it.collect { it.PartnerNumber } }?.toSet()?.toList()

if (!customerIds) return [:]

def t2 = qapi.tables().customers()
def customerFilter = qapi.exprs().and(
        t2.customerId().in(customerIds),
        qapi.exprs().or(
                t2.CustomerOrderBlock.isNull(),
                t2.CustomerOrderBlock.equal("")
        ),
        qapi.exprs().or(
                t2.CustomerDeliveryBlock.isNull(),
                t2.CustomerDeliveryBlock.equal("")
        ),
        qapi.exprs().or(
                t2.BillingBlock.isNull(),
                t2.BillingBlock.equal("")
        )
)
def fields = [t2.customerId(), t2.name(), t2.City, t2.Region]

return qapi.source(t2, fields, customerFilter).stream {
    it.collect {
        it.customerId + " - " + (it.name ?: "") + " - " + (it.City ?: "") + " - " + (it.Region ?: "")
    }
} ?: [:]