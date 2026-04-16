if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.batchKeys1) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("UOMConversion")

    def uoms
    api.global.uomOptions = qapi.source(t1, [t1.key1(), t1.key2()], t1.key1().in(api.global.batchKeys1 as List)).stream { it.collect { it } }?.
            groupBy { it.key1 }?.
            collectEntries { key1, items ->
                uoms = items*.key2 ?: []
                uoms?.add("KG")
                uoms?.sort()
                uoms?.unique()
                [key1, uoms]
            } ?: [:]
}

return api.global.uomOptions?.get(api.local.sku) ?: []