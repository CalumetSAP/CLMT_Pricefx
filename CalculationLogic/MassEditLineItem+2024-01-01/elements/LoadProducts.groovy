def material = api.local.material

// when the new batch starts, do pre-load products (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def filters = [
            Filter.in("sku", api.global.currentBatch)
    ]
    def fields = ["sku", "label", "unitOfMeasure", "attribute1", "attribute2", "attribute4", "attribute13", "attribute14", "attribute15",
                  "attribute16", "attribute17", "attribute18", "attribute19", "attribute20", "attribute21"]
    api.global.products = api.stream("P", "sku", fields, *filters)?.withCloseable {
        it.collectEntries {
            [(it.sku): [
                    Description             : it.label,
                    UOM                     : it.unitOfMeasure,
                    BrandCode               : it.attribute2,
                    BrandName               : it.attribute13,
                    NetWeight               : it.attribute4,
                    PH1                     : it.attribute14,
                    PH1CodeAndDescription   : it.attribute14 ? it.attribute14+" - "+it.attribute15 : null,
                    PH2CodeAndDescription   : it.attribute16 ? it.attribute16+" - "+it.attribute17 : null,
                    PH3CodeAndDescription   : it.attribute18 ? it.attribute18+" - "+it.attribute19 : null,
                    PH4CodeAndDescription   : it.attribute20 ? it.attribute20+" - "+it.attribute21 : null,
            ]]
        }
    }
}

api.global.products?.get(material)