if (api.isInputGenerationExecution() || !api.local.lineItemSkus) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def filter = Filter.in("key1", api.local.lineItemSkus)
def fields = ["key1", "key2", "attribute1", "attribute2"]

def uomConversionMap = [:]
def uomOptions = [:]

api.findLookupTableValues(tablesConstants.UOM_CONVERSION, fields, null, filter)?.each {
    if (!uomOptions.containsKey(it.key1)) uomOptions[it.key1] = []
    uomOptions[it.key1].add(it.key2)
    uomConversionMap.put((it.key1 + "|" + it.key2), [Numerator  : it.attribute1?.toBigDecimal(),
                                                     Denominator: it.attribute2?.toBigDecimal(),])
}

api.local.uomOptions = uomOptions

return uomConversionMap