if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.batchKeys1) {
    def items = api.global.batchKeys1?.toSet()?.toList()
    List<String> plants = api.global.plantList as List

    if (plants) {
        def qapi = api.queryApi()

        def t1 = qapi.tables().productExtensionRows("Cost")
        def fields = [t1.sku(), t1."Attribute 1", t1."Attribute 2"]
        def filter = qapi.exprs().and(
                t1.sku().in(items),
                t1."Attribute 2".in(plants)
        )

        api.global.costPX = qapi.source(t1, fields, filter)
                .stream {
                    it.collectEntries {
                        [(it.sku + "|" + it."Attribute 2"): it."Attribute 1" ]
                    }
                } ?: [:]
    } else {
        api.global.costPX = [:]
    }
}

return api.global.costPX.get(api.local.sku + "|" + out.LoadQuotes.Plant)