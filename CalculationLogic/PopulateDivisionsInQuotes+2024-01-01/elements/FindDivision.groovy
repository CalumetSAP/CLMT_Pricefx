if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def division = api.findLookupTableValues(tablesConstants.DIVISION)?.collectEntries { [(it.name): it.name + " (" + it.attribute1 + ")"] }

return division