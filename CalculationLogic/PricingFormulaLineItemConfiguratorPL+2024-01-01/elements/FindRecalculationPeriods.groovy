if (api.global.recalculationPeriods) return api.global.recalculationPeriods

def filters = [
        Filter.equal("key1", "Quote"),
        Filter.equal("key2", "RecalculationPeriod")
]
api.global.recalculationPeriods = api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.DROPDOWN_OPTIONS, *filters)?.collect { it.attribute1 } ?: []

return api.global.recalculationPeriods