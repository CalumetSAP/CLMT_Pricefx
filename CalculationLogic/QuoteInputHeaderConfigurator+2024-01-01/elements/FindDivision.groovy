if (api.isInputGenerationExecution()) return

def selectedSoldTo = out.InputSoldTo?.getFirstInput()?.getValue()

if (!selectedSoldTo) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def customerFields = ["attribute3"]
def customerFilter = [
        Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
        Filter.equal("customerId", selectedSoldTo),
        Filter.in("attribute4", ["SP", "AG"])
]

def divisionList = api.stream("CX", null, customerFields, *customerFilter)
        ?.withCloseable { it.collect { it.attribute3 } }
        ?.unique()

return divisionList