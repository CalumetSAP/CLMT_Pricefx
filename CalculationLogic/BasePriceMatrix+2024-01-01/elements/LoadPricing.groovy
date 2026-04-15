if (api.isInputGenerationExecution()) api.abortCalculation()

if (api.global.isFirstRow) {
    def today = new Date()

    def customFilter = Filter.and(
            Filter.in("PricelistID", api.local.pricelist),
            Filter.lessOrEqual("EffectiveDate", today),
            Filter.greaterOrEqual("ExpirationDate", today),
    )

    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("BasePricing")

    def query = ctx.newQuery(ds, false)
            .select("ProductId", "ProductId")
            .select("PricelistID", "PricelistID")
            .where(customFilter)

    def result = ctx.executeQuery(query)

    api.global.pricing = result?.getData()?.groupBy { it.ProductId }?.collectEntries { productId, records ->
        [productId, records*.PricelistID]
    }
}

return null
