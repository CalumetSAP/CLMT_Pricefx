if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def data = api.findLookupTableValues(tablesConstants.INCO_TERM)

return data.collect { it.name + " - " + it.attribute1 }