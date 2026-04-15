if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final calculations = libs.QuoteLibrary.Calculations

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedSoldTo = customerConfigurator?.get(headerConstants.SOLD_TO_ID)
def shipTos = api.local.lineItemShipTos ? api.local.lineItemShipTos as List : []
def lineItemSkus = api.local.lineItemSkus ? api.local.lineItemSkus as List : []

def filters = [
        Filter.equal("key2", selectedSoldTo),
        Filter.in("key3", shipTos + ["*"]),
        Filter.in("key4", lineItemSkus + ["*"]),
]

def data = api.findLookupTableValues(tablesConstants.EXCLUSIONS, null, null, *filters)

return calculations.groupExclusionsData(data)