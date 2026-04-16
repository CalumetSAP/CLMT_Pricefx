if (api.isInputGenerationExecution() || (!api.local.lineItemSkus && !api.local.addedContracts)) return

def skus = []
if (api.local.lineItemSkus) {
    skus = api.local.lineItemSkus
} else {
    def contracts = out.FindContractDSData
    contracts?.each { contractNumber, lines ->
        lines?.each { line ->
            skus.add(line?.Material)
        }
    }
}
skus = skus?.unique()

if (!skus) return [:]

def qapi = api.queryApi()

def t1 = qapi.tables().products()

def fields = [
        t1.sku(),
        t1.label(),
        t1.unitOfMeasure(),
        t1."Attribute 4",
        t1."Attribute 6",
        t1."Attribute 9",
        t1.OldMaterialNumber,
        t1."Attribute 14",
        t1."Attribute 15",
        t1."Attribute 16",
        t1."Attribute 17",
        t1."Attribute 18",
        t1."Attribute 19",
        t1."Attribute 20",
        t1."Attribute 21",
        t1.ContainerDesc,
        t1.ContainerCategory,
]

def products = qapi.source(t1, fields, t1.sku().in(skus)).stream { it.collectEntries {
    [(it.sku): [
            Material            : it.sku,
            Description         : it.label,
            UOM                 : it.unitOfMeasure,
            SalesUnit           : it."Attribute 9",
            LegacyMaterialNumber: it.OldMaterialNumber,
            MaterialPackageStyle: it.ContainerCategory,
            ContainerDescription: it.ContainerDesc,
            Division            : it."Attribute 6",
            PH1Code             : it."Attribute 14" ?: null,
            PH1Description      : it."Attribute 15" ?: null,
            PH2Code             : it."Attribute 16" ?: null,
            PH2Description      : it."Attribute 17" ?: null,
            PH3Code             : it."Attribute 18" ?: null,
            PH3Description      : it."Attribute 19" ?: null,
            PH4Code             : it."Attribute 20" ?: null,
            PH4Description      : it."Attribute 21" ?: null,
            NetWeight           : it."Attribute 4"?.toBigDecimal() ?: null,
    ]] } } ?: [:]

return products