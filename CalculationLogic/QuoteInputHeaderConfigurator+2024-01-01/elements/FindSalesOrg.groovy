if (api.isInputGenerationExecution()) return

def selectedDivision = out.InputDivision?.getFirstInput()?.getValue()

if (!selectedDivision) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def selectedSoldTo = out.InputSoldTo?.getFirstInput()?.getValue()

def qapi = api.queryApi()
def t1 = qapi.tables().customerExtensionRows(tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION)
def filter = qapi.exprs().and(
        t1.customerId().equal(selectedSoldTo),
        t1.Division.equal(selectedDivision),
        t1.PartnerFunction.in(["SP", "AG"])
)

return qapi.source(t1, [t1.SalesOrg], filter).stream { it.collect { it.SalesOrg } }?.toSet()?.toList()