final tablesConstants = libs.QuoteConstantsLibrary.Tables

def pricelists = api.findLookupTableValues(tablesConstants.PRICELIST)?.collectEntries { [(it.name): it.name + " - " + it.attribute1] }

return pricelists ?: [:]