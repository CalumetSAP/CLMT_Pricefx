if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.batchKeys1) {
    def items = api.global.batchKeys1.toSet().toList()

    def qapi = api.queryApi()
    def exprs = qapi.exprs()

    def t1 = qapi.tables().companyParameterRows("STCCtoMaterial")

    def filter = exprs.or(
            t1.key1().in(items),
            t1.key1().in(items.collect { it.take(6)})
    )

    api.global.stccToMaterial = qapi.source(t1, [t1.key1(), t1.STCC], filter)
            .stream { it.collectEntries { [(it.key1): it.STCC] } } ?: [:]
}

def material = api.local.sku
def stccToMaterial = api.global.stccToMaterial

return [
        fullSTCC        : stccToMaterial.get(material),
        sixDigitsSTCC   : stccToMaterial.get(material.take(6))
]