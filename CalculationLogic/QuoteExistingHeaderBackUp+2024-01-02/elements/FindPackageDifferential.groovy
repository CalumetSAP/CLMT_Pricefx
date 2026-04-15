if (api.isInputGenerationExecution() || !api.local.lineItemSkus) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def containerCodes = api.local.lineItemSkus?.collect { sku ->
    sku.size() > 6 ? sku.substring(sku.size() - 3, sku.size()) : sku
}

def filter = Filter.in("key1", containerCodes)
def fields = ["key1", "key2", "attribute3", "attribute5"]

return api.findLookupTableValues(tablesConstants.PACKAGE_DIFFERENTIALS, fields, null, filter)?.collectEntries {
    [(it.key1 + "|" + it.key2): [
            Value: it.attribute3?.toBigDecimal(),
            UOM  : it.attribute5
    ]
    ]
}