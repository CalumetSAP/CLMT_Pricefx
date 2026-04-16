if (api.global.isFirstRow) {
    def filters = [
            Filter.equal("lookupTable.name", libs.QuoteConstantsLibrary.Tables.INDEX_VALUES),
            Filter.equal("lookupTable.status", "Active"),
    ]

    api.global.indexValueOptions = api.stream("MLTV4", null, ["key1", "key2", "attribute1"], true, *filters)?.withCloseable{it.collect { it.key1 + "-" + it.key2 + "-" + it.attribute1 } } ?: []
}

return api.global.indexValueOptions