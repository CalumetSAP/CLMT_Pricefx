String index1Override = api.getManualOverride("Index1")
String index2Override = api.getManualOverride("Index2")
String index3Override = api.getManualOverride("Index3")

if (api.global.isFirstRow || index1Override || index2Override || index3Override) {
    final indexLib = libs.PricelistLib.Index
    Date calculationDate = api.global.calculationDate
    String lastDaysOfPreviousQuarterReferencePeriod
    if (api.getManualOverride("ReferencePeriod")) {
        lastDaysOfPreviousQuarterReferencePeriod = out.LoadQuotes?.ReferencePeriod
    } else {
        lastDaysOfPreviousQuarterReferencePeriod = "6"
    }
    //TODO "LoadLastDaysOfPreviousMonth" has similar code, try to eliminate duplicated
    Set<String> indexs = api.global.quotes
            ?.findAll { it.ReferencePeriod == lastDaysOfPreviousQuarterReferencePeriod }
            ?.collect { [it.IndexNumberOne, it.IndexNumberTwo, it.IndexNumberThree] }
            ?.flatten()
            ?.toSet()
    indexs.addAll([index1Override, index2Override, index3Override])
    indexs.remove(null)
    Set<String> key1s = new HashSet<>()
    Set<String> key2s = new HashSet<>()
    Set<String> attribute1s = new HashSet<>()

    LinkedHashMap<String, String> indexValuesKeys
    for (index in indexs) {
        indexValuesKeys = indexLib.getIndexValuesKeys(index)
        key1s.add(indexValuesKeys.key1)
        key2s.add(indexValuesKeys.key2)
        attribute1s.add(indexValuesKeys.attribute1)
    }

    List filters = [
            Filter.equal("lookupTable.name", "IndexValues"),
            Filter.equal("lookupTable.status", "Active"),
            Filter.in("key1", key1s),
            Filter.in("key2", key2s),
            Filter.in("attribute1", attribute1s),
    ]
    filters.addAll(indexLib.getLastDayOfQuarterFilter(calculationDate))

    api.global.lastDaysOfPreviousQuarter = api.findLookupTableValues("IndexValues", ["key1", "key2", "key3", "attribute1", "attribute2", "attribute4", "attribute5"], "-key3", *filters)?.groupBy {
        ("${it.key1}-${it.key2}-${it.attribute1}" as String)
    }?.collectEntries { key, indexValueRows ->
        [(key): indexValueRows?.find() ?: [:]]
    } ?: [:]
}

return [
        api.global.lastDaysOfPreviousQuarter[api.local.index1],
        api.global.lastDaysOfPreviousQuarter[api.local.index2],
        api.global.lastDaysOfPreviousQuarter[api.local.index3],
]