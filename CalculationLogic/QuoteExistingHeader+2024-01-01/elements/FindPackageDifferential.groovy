if (api.isInputGenerationExecution() || (!api.local.lineItemSkus && !api.local.addedContracts)) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

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

def containerCodes = skus?.collect { sku ->
    sku.size() > 6 ? sku.substring(sku.size() - 3, sku.size()) : sku
}

def filter = Filter.in("key1", containerCodes)
def fields = ["key1", "key2", "attribute3", "attribute5"]

return api.findLookupTableValues(tablesConstants.PACKAGE_DIFFERENTIALS, fields, null, filter)?.collectEntries {
    [(it.key1 + "|" + it.key2): [
            Value: it.attribute3?.toBigDecimal(),
            UOM  : it.attribute5
    ]
    ]
}