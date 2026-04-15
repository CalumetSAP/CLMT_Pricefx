if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def data = api.findLookupTableValues(tablesConstants.MEANS_OF_TRANSPORTATION)

return data.collect { it.name + " - " + it.attribute1 }