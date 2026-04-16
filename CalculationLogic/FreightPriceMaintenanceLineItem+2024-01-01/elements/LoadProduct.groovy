if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.batchKeys1) {
    def qapi = api.queryApi()

    def t1 = qapi.tables().products()

    def fields = [t1.sku(), t1.label(), t1."Attribute 14", t1."Attribute 15", t1."Attribute 16", t1."Attribute 17",
                  t1."Attribute 18", t1."Attribute 19", t1."Attribute 20", t1."Attribute 21", t1.unitOfMeasure()]

    def items = api.global.batchKeys1?.toSet()?.toList()

    api.global.products = qapi.source(t1, fields, t1.sku().in(items))
            .stream {
                it.collectEntries {
                    [(it.sku): [
                            Description          : it.label,
                            UOM                  : it.unitOfMeasure,
                            PH1                  : it."Attribute 14",
                            PH1CodeAndDescription: it."Attribute 14" ? it."Attribute 14" + " - " + it."Attribute 15" : null,
                            PH2CodeAndDescription: it."Attribute 16" ? it."Attribute 16" + " - " + it."Attribute 17" : null,
                            PH3CodeAndDescription: it."Attribute 18" ? it."Attribute 18" + " - " + it."Attribute 19" : null,
                            PH4CodeAndDescription: it."Attribute 20" ? it."Attribute 20" + " - " + it."Attribute 21" : null,
                    ]]
                }
            } ?: [:]
}

return api.global.products.get(api.local.sku) ?: [:]