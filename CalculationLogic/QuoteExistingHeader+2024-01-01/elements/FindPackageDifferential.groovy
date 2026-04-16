if (api.isInputGenerationExecution() || (!api.local.lineItemSkus && !api.local.addedContracts)) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

Set<String> skuSet = [] as Set
if (api.local.lineItemSkus) {
    skuSet.addAll(api.local.lineItemSkus)
}
if (api.local.addedContracts) {
    (out.FindContractDSData ?: [:]).each { _, lines ->
        lines?.each { skuSet << it?.Material }
    }
}
if (skuSet.isEmpty()) return [:]
def skus = skuSet.toList()

def containerCodes = skus?.collect { sku ->
    sku.size() > 6 ? sku.substring(sku.size() - 3, sku.size()) : sku
}

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows(tablesConstants.PACKAGE_DIFFERENTIALS)

def fields = [
        t1.key1(),
        t1.key2(),
        t1.PriceDifferential,
        t1.BaseUnitOfMeasure
]

return qapi.source(t1, fields, t1.key1().in(containerCodes))
        .stream { it.collectEntries {
            [(it.key1 + "|" + it.key2): [
                    Value: it.PriceDifferential?.toBigDecimal(),
                    UOM  : it.BaseUnitOfMeasure
            ]]
        }}