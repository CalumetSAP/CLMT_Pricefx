String material = api.local.material
String pricelist = api.local.pricelist
Date newEffectiveDate = api.local.newEffectiveDate

// when the new batch starts, do pre-load products (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def materials = api.global.currentBatch

    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("BasePricing")

    def customFilter = Filter.and(
            Filter.in("ProductId", materials),
            Filter.in("PricelistID", api.global.pricelists),
    )

    def query = ctx.newQuery(dm, false)
            .select("EffectiveDate", "EffectiveDate")
            .select("ExpirationDate", "ExpirationDate")
            .select("ProductId", "ProductId")
            .select("PricelistID", "PricelistID")
            .select("JobberPercent", "JobberPercent")
            .select("SRPPercent", "SRPPercent")
            .select("MAPPercent", "MAPPercent")
            .where(customFilter)
            .orderBy("lastUpdateDate DESC")

    api.global.PricingMap = ctx.executeQuery(query)?.getData()?.groupBy { [it.ProductId, it.PricelistID] }
}

if (pricelist) {
    return api.global.PricingMap?.get([material, pricelist])?.find {
        it.EffectiveDate <= newEffectiveDate && it.ExpirationDate >= newEffectiveDate
    } ?: [:]
} else {
    return null
}