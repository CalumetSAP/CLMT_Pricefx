if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def data = api.findLookupTableValues(tablesConstants.INCO_TERM)

return data?.collectEntries { [(it.name): it.name + " - " + it.attribute1] }