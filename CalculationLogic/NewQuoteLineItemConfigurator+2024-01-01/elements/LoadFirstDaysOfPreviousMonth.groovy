//if (api.isInputGenerationExecution()) return
//
//def indexValues = PFIndexNumber?.entry?.getFirstInput()?.getValue()
//def lastDaysOfPreviousMonthReferencePeriod = PFReferencePeriod?.entry?.getFirstInput()?.getValue()
//
//def priceType = InputPriceType?.input?.getValue()
//
//if (priceType != "1" || !(api.local.indexHasChanged || api.local.priceHasChanged || api.local.adderHasChanged || api.local.freightAmountHasChanged) || !indexValues || lastDaysOfPreviousMonthReferencePeriod != "11") return
//
//final indexLib = libs.PricelistLib.Index
//Date calculationDate = libs.QuoteLibrary.DateUtils.getToday()
//
//Set<String> key1s = new HashSet<>()
//Set<String> key2s = new HashSet<>()
//Set<String> attribute1s = new HashSet<>()
//
//LinkedHashMap<String, String> indexValuesKeys
//for (index in indexValues) {
//    indexValuesKeys = indexLib.getIndexValuesKeys(index)
//    key1s.add(indexValuesKeys.key1)
//    key2s.add(indexValuesKeys.key2)
//    attribute1s.add(indexValuesKeys.attribute1)
//}
//
//List filters = [
//        Filter.equal("lookupTable.name", "IndexValues"),
//        Filter.equal("lookupTable.status", "Active"),
//        Filter.in("key1", key1s),
//        Filter.in("key2", key2s),
//        Filter.in("attribute1", attribute1s),
//]
//filters.addAll(indexLib.getPreviousMonthAverageFilters(calculationDate))
//
//api.local.lastDaysOfPreviousMonth = api.findLookupTableValues("IndexValues", ["key1", "key2", "key3", "attribute1", "attribute2", "attribute4", "attribute5"], "key3", *filters)?.groupBy {
//    ("${it.key1}-${it.key2}-${it.attribute1}" as String)
//}?.collectEntries { key, indexValueRows ->
//    [(key): indexValueRows?.find() ?: [:]]
//} ?: [:]
//
//return [
//        api.local.lastDaysOfPreviousMonth[api.local.index1],
//        api.local.lastDaysOfPreviousMonth[api.local.index2],
//        api.local.lastDaysOfPreviousMonth[api.local.index3],
//]