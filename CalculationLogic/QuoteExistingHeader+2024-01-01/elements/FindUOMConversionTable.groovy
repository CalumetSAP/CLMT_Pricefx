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

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def filter = Filter.in("key1", skus)
def fields = ["key1", "key2", "attribute1", "attribute2"]

def uomOptions = [:]

api.findLookupTableValues(tablesConstants.UOM_CONVERSION, fields, null, filter)?.each {
    if (!uomOptions.containsKey(it.key1)) uomOptions[it.key1] = []
    uomOptions[it.key1].add(it.key2)
}

api.local.uomOptions = uomOptions

return libs.QuoteLibrary.Conversion.getUOMConversion(skus, out.FindProductMasterData)