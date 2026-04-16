if (api.global.isFirstRow) {
    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.equal("key2", "ReferencePeriod")
    ]
    api.global.referencePeriodOptions = api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.DROPDOWN_OPTIONS, ["key3", "attribute1"], "key3", *filters)?.collectEntries { [(it.attribute1): it.key3] }
}

return api.global.referencePeriodOptions ?: [:]