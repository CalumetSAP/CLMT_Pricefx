def material = api.local.material

// when the new batch starts, do pre-load products (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def uoms

    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("UOMConversion")

    api.global.uomOptions = qapi.source(t1, [t1.key1(), t1.key2()], t1.key1().in(api.global.currentBatch as List)).stream { it.collect() }?.
            groupBy { it.key1 }?.
            collectEntries { key1, items ->
                uoms = items*.key2 ?: []
                uoms?.add("KG")
                uoms?.sort()
                uoms?.unique()
                [key1, uoms]
            } ?: [:]
}

return api.global.uomOptions?.get(material) ?: []