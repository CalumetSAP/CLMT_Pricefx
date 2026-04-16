def material = api.local.material

// when the new batch starts, do pre-load products (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def filters = [
            Filter.in("sku", api.global.currentBatch)
    ]
    def fields = ["sku", "label", "unitOfMeasure", "attribute1"]
    def rowIterator = api.stream("P", "sku", fields, *filters)

    api.global.products = rowIterator?.collectEntries {
        [(it.sku): [
                Description         : it.label,
                UOM                 : it.unitOfMeasure,
                ProductHierarchy    : it.attribute1
        ]]
    }
    rowIterator?.close()
}

return api.global.products?.get(material)