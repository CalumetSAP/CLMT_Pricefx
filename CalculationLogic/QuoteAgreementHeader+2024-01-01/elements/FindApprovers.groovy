if (!quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final calculations = libs.QuoteLibrary.Calculations

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedDivision = customerConfigurator?.get(headerConstants.DIVISION_ID)
def selectedSalesOrg = customerConfigurator?.get(headerConstants.SALES_ORG_ID)
if (!selectedDivision || !selectedSalesOrg) return [:]

def filter = [
        Filter.equal("key2", selectedDivision),
        Filter.equal("key1", selectedSalesOrg),
]
def fields = ["key3", "key4", "key5", "attribute3"]

def data = api.findLookupTableValues(tablesConstants.APPROVERS_TABLE, fields, null, *filter)

return calculations.groupApproversData(data)