if (api.isInputGenerationExecution()) return []

def soldToValues = out.GetSoldToValues

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def qapi = api.queryApi()
def t1 = qapi.tables().customerExtensionRows(tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION)
def extensionFilter = soldToValues
        ? qapi.exprs().and(t1.customerId().in(soldToValues), t1.PartnerFunction.in(["SH", "WE"]))
        : t1.customerId().isNotNull()

def customerIds = qapi.source(t1, [t1.PartnerNumber], extensionFilter).stream { it.collect { it.PartnerNumber } } ?: []

if (!customerIds) return [:]

def t2 = qapi.tables().customers()
def customerFilter = t2.customerId().in(customerIds)
def fields = [t2.customerId(), t2.name(), t2.City, t2.Region]

return qapi.source(t2, fields, customerFilter).stream {
    it.collectEntries {
        [(it.customerId): it.customerId + " - " + (it.name ?: "") + " - " + (it.City ?: "") + " - " + (it.Region ?: "") ]
    }
} ?: [:]