def material = api.local.material

// when the new batch starts, do pre-load products (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def filter = Filter.in("key1", api.global.currentBatch)
    def fields = ["key1", "key2"]
    def uoms
    api.global.uomOptions = api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.UOM_CONVERSION, fields, null, filter)?.groupBy { it.key1 }?.collectEntries { key1, items ->
        uoms = items*.key2 ?: []
        uoms?.add("KG")
        uoms?.sort()
        uoms?.unique()
        [key1, uoms]
    } ?: [:]
}

return api.global.uomOptions[material] ?: []