if (api.global.referencePeriods) return api.global.referencePeriods

def filters = [
        Filter.equal("key1", "Quote"),
        Filter.equal("key2", "ReferencePeriod")
]
api.global.referencePeriods = api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.DROPDOWN_OPTIONS, *filters)?.collectEntries {
    [(it.key3) : it.attribute1]
} ?: [:]

return api.global.referencePeriods