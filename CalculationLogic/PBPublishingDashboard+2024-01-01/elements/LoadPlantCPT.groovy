final tablesConstants = libs.QuoteConstantsLibrary.Tables

def records = api.find("MLTV", 0, 2000, null, null, Filter.equal("lookupTable.name", tablesConstants.PLANT))?.collectEntries { [(it.name): [it.attribute10, it.attribute18]?.join(", ")] }

api.global.plant = records

return records