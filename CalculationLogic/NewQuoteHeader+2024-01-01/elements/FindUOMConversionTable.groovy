if (api.isInputGenerationExecution() || !api.local.lineItemSkus) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def filter = Filter.in("key1", api.local.lineItemSkus)
def fields = ["key1", "key2", "attribute1", "attribute2"]

def uomOptions = [:]

api.findLookupTableValues(tablesConstants.UOM_CONVERSION, fields, null, filter)?.each {
    if (!uomOptions.containsKey(it.key1)) uomOptions[it.key1] = []
    uomOptions[it.key1].add(it.key2)
}

api.local.uomOptions = uomOptions

return libs.QuoteLibrary.Conversion.getUOMConversion(api.local.lineItemSkus, out.FindProductMasterData)