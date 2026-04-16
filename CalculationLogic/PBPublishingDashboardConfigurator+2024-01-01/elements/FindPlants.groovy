final tablesConstants = libs.QuoteConstantsLibrary.Tables

def fields = ["name", "attribute1"]
def plants = api.findLookupTableValues(tablesConstants.PLANT, fields, null, null)?.collectEntries { [(it.name): it.name + " - " + it.attribute1] } ?: [:]

return plants