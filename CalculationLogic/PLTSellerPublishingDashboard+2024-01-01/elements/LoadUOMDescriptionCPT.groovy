final tablesConstants = libs.QuoteConstantsLibrary.Tables

def records = api.find("MLTV", 0, 2000, null, null, Filter.equal("lookupTable.name", tablesConstants.UOM_DESCRIPTION))?.collectEntries { [(it.name): it.attribute1] }

api.global.uomDescription = records

return records