if (api.isInputGenerationExecution() || (!api.local.lineItemSkus && !api.local.addedContracts)) return

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

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def uomOptions = [:]

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows(tablesConstants.UOM_CONVERSION)
qapi.source(t1, [t1.key1(), t1.key2()], t1.key1().in(skus)).stream {it.collect() }?.each {
    if (!uomOptions.containsKey(it.key1)) uomOptions[it.key1] = []
    uomOptions[it.key1].add(it.key2)
}

api.local.uomOptions = uomOptions

return libs.QuoteLibrary.Conversion.getUOMConversion(skus, out.FindProductMasterData)