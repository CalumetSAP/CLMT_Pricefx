if (api.global.indexNumbers) return api.global.indexNumbers

def filters = [
        Filter.equal("lookupTable.name", libs.QuoteConstantsLibrary.Tables.INDEX_VALUES),
        Filter.equal("lookupTable.status", "Active"),
]
api.global.indexNumbers = api.stream("MLTV4", null, ["key1", "key2", "attribute1"], true, *filters)?.withCloseable {
    it.collect { it.key1 + "-" + it.key2 + "-" + it.attribute1 }
} ?: []

return api.global.indexNumbers