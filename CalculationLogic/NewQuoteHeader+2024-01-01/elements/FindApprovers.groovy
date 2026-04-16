if (api.isInputGenerationExecution() || !api.local.lineItemSkus) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final calculations = libs.QuoteLibrary.Calculations

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedDivision = headerConfigurator?.get(headerConstants.DIVISION_ID)
def selectedSalesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)
if (!selectedDivision || !selectedSalesOrg) return [:]

def filter = [
        Filter.equal("key2", selectedDivision),
        Filter.equal("key1", selectedSalesOrg),
]
def fields = ["key3", "key4", "key5", "key6", "attribute3"]

def data = api.findLookupTableValues(tablesConstants.APPROVERS_TABLE, fields, "lastUpdateDate", *filter)

return calculations.groupApproversData(data)