def material = api.local.material

// when the new batch starts, do pre-load BasePricing (DS) (for all SKUs of the batch) into memory
if (api.global.isFirstRow) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("BasePricing")

    def today = new Date()

    def customFilter = Filter.and(
            Filter.in("PricelistID", api.local.pricelists),
            Filter.lessOrEqual("EffectiveDate", today),
            Filter.greaterOrEqual("ExpirationDate", today),
    )

    def query = ctx.newQuery(dm, false)
            .select("ProductId", "ProductId")
            .select("PricelistID", "PricelistID")
            .select("BasePrice", "BasePrice")
            .select("JobberPrice", "JobberPrice")
            .select("MAP", "MAP")
            .select("SRP", "SRP")
            .select("UOM", "UOM")
            .select("EffectiveDate", "EffectiveDate")
            .select("ExpirationDate", "ExpirationDate")
            .where(customFilter)
            .orderBy("EffectiveDate DESC", "ExpirationDate DESC")

    def result = ctx.executeQuery(query)
    def data = result?.getData()
    def pricingMap = [:]

    data?.each { row ->
        def productId = row.ProductId
        def pricelistId = row.PricelistID

        if (!pricingMap.containsKey(productId)) {
            pricingMap[productId] = [:]
        }

        pricingMap[productId][pricelistId] = row
    }

    api.global.PricingMap = pricingMap
}

return api.global.PricingMap?.get(material)?.get(api.local.pricelist)
