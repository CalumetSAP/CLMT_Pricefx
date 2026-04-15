def material = api.local.material

// when the new batch starts, do pre-load product costs (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def filters = [
            Filter.equal("name", "Cost"),
            Filter.in("sku", api.global.currentBatch),
            Filter.isNotNull("attribute5"),
            Filter.notEqual("attribute5", ""),
            Filter.or(
                    Filter.notEqual("attribute1", "Z1"), Filter.isNull("attribute1"), Filter.equal("attribute1", "")
            )
    ]
    def fields = ["sku", "attribute3", "attribute5", "attribute6"]
    def rowIterator = api.stream("PX", "sku", fields, *filters)

    def skuAverages = [:]

    rowIterator?.each { row ->
        def sku = row.sku
        def cost = row.attribute5.replaceAll(",", "").toBigDecimal()
        def currency = row.attribute6
        def currencyConversion = out.LoadCurrencyConversion?.get(currency)

        if(currencyConversion){
            cost = cost * currencyConversion

            def size = row.attribute3.replaceAll(",", "").toBigDecimal()

            if (!skuAverages.containsKey(sku)) {
                skuAverages[sku] = [sum: cost, costingLotSize: size, count: 1]
            } else {
                skuAverages[sku].sum += cost
                skuAverages[sku].costingLotSize += size
                skuAverages[sku].count += 1
            }
        }
    }

    api.global.productCosts = skuAverages.collectEntries { sku, values ->
        def average = values.sum / values.costingLotSize / values.count
        [(sku): average]
    }

    rowIterator?.close()
}

api.global.productCosts?.get(material)