final tablesConstants = libs.QuoteConstantsLibrary.Tables

def records = api.find("MLTV", 0, 2000, null, null, Filter.equal("lookupTable.name", "CurrencyDecimals"))?.collectEntries { [(it.name): it.attribute1] }

api.global.currencyDecimals = records

return records