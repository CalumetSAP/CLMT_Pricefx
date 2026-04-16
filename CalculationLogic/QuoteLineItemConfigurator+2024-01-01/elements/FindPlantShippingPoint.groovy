if (api.isInputGenerationExecution() || !out.InputPlant?.getFirstInput()?.getValue()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final calculations = libs.QuoteLibrary.Calculations

def filter = Filter.equal("key1", calculations.removePlantDescription([out.InputPlant?.getFirstInput()?.getValue()])?.find())

def fields = ["key1", "key2"]
def shippingPoints = api.findLookupTableValues(tablesConstants.PLANT_SHIPPING_POINT, fields, null, filter)?.key2

return shippingPoints?.collect { api.local.shippingPoints?.get(it) }