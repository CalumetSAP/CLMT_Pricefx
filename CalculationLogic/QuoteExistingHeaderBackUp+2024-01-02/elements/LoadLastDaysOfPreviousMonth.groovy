if (api.isInputGenerationExecution() || !quoteProcessor.isPrePhase()) return

def indexValuesList = api.local.indexValuesList
//def lastDaysOfPreviousMonthReferencePeriod = PFReferencePeriod?.entry?.getFirstInput()?.getValue()
//
//def priceType = InputPriceType?.input?.getValue()
//
if (!indexValuesList) return

final indexLib = libs.PricelistLib.Index
Date calculationDate = libs.QuoteLibrary.DateUtils.getToday()

Set<String> key1s = new HashSet<>()
Set<String> key2s = new HashSet<>()
Set<String> attribute1s = new HashSet<>()

LinkedHashMap<String, String> indexValuesKeys
for (indexValues in indexValuesList) {
    for (index in indexValues) {
        indexValuesKeys = indexLib.getIndexValuesKeys(index)
        key1s.add(indexValuesKeys.key1)
        key2s.add(indexValuesKeys.key2)
        attribute1s.add(indexValuesKeys.attribute1)
    }
}

List filters = [
        Filter.equal("lookupTable.name", "IndexValues"),
        Filter.equal("lookupTable.status", "Active"),
        Filter.in("key1", key1s),
        Filter.in("key2", key2s),
        Filter.in("attribute1", attribute1s),
]
filters.addAll(indexLib.getPreviousMonthAverageFilters(calculationDate))

api.local.lastDaysOfPreviousMonth = api.findLookupTableValues("IndexValues", ["key1", "key2", "key3", "attribute1", "attribute2", "attribute4", "attribute5"], "-key3", *filters)?.groupBy {
    ("${it.key1}-${it.key2}-${it.attribute1}" as String)
}?.collectEntries { key, indexValueRows ->
    [(key): indexValueRows?.find() ?: [:]]
} ?: [:]

return null