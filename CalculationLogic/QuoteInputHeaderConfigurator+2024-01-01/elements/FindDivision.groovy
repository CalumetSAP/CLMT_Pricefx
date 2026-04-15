if (api.isInputGenerationExecution()) return

def selectedSoldTo = out.InputSoldTo?.getFirstInput()?.getValue()

if (!selectedSoldTo) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def qapi = api.queryApi()
def t1 = qapi.tables().customerExtensionRows(tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION)
def filter = qapi.exprs().and(
        t1.customerId().equal(selectedSoldTo),
        t1.PartnerFunction.in(["SP", "AG"])
)

return qapi.source(t1, [t1.Division], filter).stream { it.collect { it.Division } }?.toSet()?.toList()