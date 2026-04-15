final tablesConstants = libs.QuoteConstantsLibrary.Tables

def fields = ["name", "attribute1"]
def pricelists = api.findLookupTableValues(tablesConstants.PRICELIST, fields, null, null)?.collectEntries { [(it.name): it.name + " - " + it.attribute1] } ?: [:]

return pricelists