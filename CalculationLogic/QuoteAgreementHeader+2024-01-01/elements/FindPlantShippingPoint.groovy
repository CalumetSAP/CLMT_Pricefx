if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.lineItemPlants) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final calculations = libs.QuoteLibrary.Calculations
final transform = libs.BdpLib.Transform

def filter = Filter.in("key1", calculations.removePlantDescription(api.local.lineItemPlants))

def fields = ["key1", "key2"]
def shippingPoints = api.findLookupTableValues(tablesConstants.PLANT_SHIPPING_POINT, fields, null, filter)?.collect()

return transform.singleKeyToDictionaryAggregation(shippingPoints, "key1", "key2")