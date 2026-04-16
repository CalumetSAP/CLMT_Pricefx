if (api.isInputGenerationExecution()) return

def selectedDivision = out.InputDivision?.getFirstInput()?.getValue()

if (!selectedDivision) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def selectedSoldTo = out.InputSoldTo?.getFirstInput()?.getValue()
def customerFields = ["attribute6"]
def customerFilter = [
        Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
        Filter.equal("customerId", selectedSoldTo),
        Filter.equal("attribute3", selectedDivision),
        Filter.in("attribute4", ["SP", "AG"])
]

def salesOrgList = api.stream("CX", null, customerFields, *customerFilter)
        ?.withCloseable { it.collect { it.attribute6 } }
        ?.unique()

return salesOrgList