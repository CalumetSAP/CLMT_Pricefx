def material = api.local.material

// when the new batch starts, do pre-load product costs (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def filters = [
            Filter.equal("name", "Cost"),
            Filter.in("sku", api.global.currentBatch),
            Filter.isNotNull("attribute5"),
            Filter.notEqual("attribute5", ""),
            Filter.equal("attribute6", "USD"),
            Filter.or(
                    Filter.notEqual("attribute1", "Z1"), Filter.isNull("attribute1"), Filter.equal("attribute1", "")
            )
    ]
    def fields = ["sku", "attribute3", "attribute4", "attribute5"]
    api.global.productCosts = api.stream("PX", "sku", fields, *filters).withCloseable {
        it.collect()
    }.groupBy {
        it.sku
    }.collectEntries {
        [(it.key): [
                average : getCostAverage(it.value),
                uom     : it.value.first().attribute4
        ]]
    }
}

return api.global.productCosts?.get(material)

def getCostAverage (List rows) {
    def rowsFiltered = rows.findAll { it.attribute5 && it.attribute5.replaceAll(",", "").toBigDecimal() > BigDecimal.ZERO }
    def size = rowsFiltered.size()
    if (size) {
        def costSum = rowsFiltered.sum { it.attribute5.replaceAll(",", "").toBigDecimal() / it.attribute3.replaceAll(",", "").toBigDecimal() }
        return costSum/size
    }
    return null
}