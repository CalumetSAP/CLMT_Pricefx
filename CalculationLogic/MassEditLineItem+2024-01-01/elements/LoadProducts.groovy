def material = api.local.material

// when the new batch starts, do pre-load products (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().products()
    def fields = [t1.sku(), t1.label(), t1.unitOfMeasure(), t1."Attribute 1", t1.BrandCode, t1."Attribute 4", t1.BrandName, t1."Attribute 14",
                  t1."Attribute 15", t1."Attribute 16", t1."Attribute 17", t1."Attribute 18", t1."Attribute 19", t1."Attribute 20", t1."Attribute 21"]
    api.global.products = qapi.source(t1, fields, t1.sku().in(api.global.currentBatch as List)).stream {
        it.collectEntries {
            [(it.sku): [
                    Description             : it.label,
                    UOM                     : it.unitOfMeasure,
                    BrandCode               : it.BrandCode,
                    BrandName               : it.BrandName,
                    NetWeight               : it."Attribute 4",
                    PH1                     : it."Attribute 14",
                    PH1CodeAndDescription   : it."Attribute 14" ? it."Attribute 14"+" - "+it."Attribute 15" : null,
                    PH2CodeAndDescription   : it."Attribute 16" ? it."Attribute 16"+" - "+it."Attribute 17" : null,
                    PH3CodeAndDescription   : it."Attribute 18" ? it."Attribute 18"+" - "+it."Attribute 19" : null,
                    PH4CodeAndDescription   : it."Attribute 20" ? it."Attribute 20"+" - "+it."Attribute 21" : null,
            ]]
        }
    }
}

api.global.products?.get(material)