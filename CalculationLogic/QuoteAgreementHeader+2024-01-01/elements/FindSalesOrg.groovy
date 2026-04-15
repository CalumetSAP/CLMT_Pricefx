if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def salesOrg = api.findLookupTableValues(tablesConstants.SALES_ORG)?.collectEntries { [(it.name): it.name + " (" + it.attribute1 + ")"] }

return salesOrg