def material = api.local.material

// when the new batch starts, do pre-load products (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().products()

    api.global.products = qapi.source(t1, [t1.sku(), t1.label(), t1.unitOfMeasure(), t1."Attribute 1"], t1.sku().in(api.global.currentBatch as List)).stream {
        it.collectEntries {
            [(it.sku): [
                    Description         : it.label,
                    UOM                 : it.unitOfMeasure,
                    ProductHierarchy    : it."Attribute 1"
            ]]
        }
    }
}

return api.global.products?.get(material)