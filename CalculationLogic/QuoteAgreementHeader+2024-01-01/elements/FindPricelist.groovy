if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def data = api.findLookupTableValues(tablesConstants.PRICELIST)

return data.collectEntries { [(it.name): it.name + " - " + it.attribute1] }