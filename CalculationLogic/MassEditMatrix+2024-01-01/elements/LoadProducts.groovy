def material = api.local.sku

// when the new batch starts, do pre-load products (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def filters = [
            Filter.in("sku", api.global.currentBatch)
    ]
    def fields = ["sku", "attribute1"]
    api.global.products = api.stream("P", "sku", fields, *filters)?.withCloseable {
        it.collectEntries {
            [(it.sku): [ProductHierarchy: it.attribute1]]
        }
    }
}

api.global.products?.get(material)