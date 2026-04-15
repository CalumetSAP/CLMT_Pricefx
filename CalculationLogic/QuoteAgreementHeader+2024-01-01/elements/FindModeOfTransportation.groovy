if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def data = api.findLookupTableValues(tablesConstants.MODE_OF_TRANSPORTATION)

return data.collect { it.name + " - " + it.attribute1 }