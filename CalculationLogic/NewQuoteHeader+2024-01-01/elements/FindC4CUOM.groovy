if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def data = api.findLookupTableValues(tablesConstants.C4C_UOM)

return data?.collectEntries { [(it.name): it.attribute1] }