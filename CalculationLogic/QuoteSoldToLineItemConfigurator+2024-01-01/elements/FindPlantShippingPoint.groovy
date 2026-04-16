if (api.isInputGenerationExecution() || !InputPlant?.input?.getValue()) return

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup
if (!havePermissions) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final calculations = libs.QuoteLibrary.Calculations

def filter = Filter.equal("key1", calculations.removePlantDescription([InputPlant?.input?.getValue()])?.find())

def fields = ["key1", "key2"]
def shippingPoints = api.findLookupTableValues(tablesConstants.PLANT_SHIPPING_POINT, fields, null, filter)?.key2

return shippingPoints?.collect { api.local.shippingPointNames?.get(it) }?.findAll()?.sort()