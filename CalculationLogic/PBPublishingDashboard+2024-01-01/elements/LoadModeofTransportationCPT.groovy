final tablesConstants = libs.QuoteConstantsLibrary.Tables

def records = api.find("MLTV", 0, 2000, null, null, Filter.equal("lookupTable.name", tablesConstants.MODE_OF_TRANSPORTATION))?.collectEntries { [(it.name): it.attribute1] }

api.global.modeOfTransportation = records

return records