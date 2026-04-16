if (api.isInputGenerationExecution() || !api.local.lineItemSkus) return

def fields = ["sku", "label", "unitOfMeasure", "attribute4", "attribute6", "attribute9", "attribute12", "attribute14", "attribute15", "attribute16", "attribute17", "attribute18", "attribute19", "attribute20", "attribute21", "attribute23", "attribute24"]
def filter = Filter.in("sku", api.local.lineItemSkus)

def products = api.stream("P", null, fields, filter)?.withCloseable {
    it.collectEntries {
        [(it.sku): [
                Material            : it.sku,
                Description         : it.label,
                UOM                 : it.unitOfMeasure,
                SalesUnit           : it.attribute9,
                LegacyMaterialNumber: it.attribute12,
                MaterialPackageStyle: it.attribute24,
                ContainerDescription: it.attribute23,
                Division            : it.attribute6,
                PH1Code             : it.attribute14 ?: null,
                PH1Description      : it.attribute15 ?: null,
                PH2Code             : it.attribute16 ?: null,
                PH2Description      : it.attribute17 ?: null,
                PH3Code             : it.attribute18 ?: null,
                PH3Description      : it.attribute19 ?: null,
                PH4Code             : it.attribute20 ?: null,
                PH4Description      : it.attribute21 ?: null,
                NetWeight           : it.attribute4?.toBigDecimal() ?: null,
        ]]
    }
}

return products