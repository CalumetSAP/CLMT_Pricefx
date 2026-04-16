String lineID = out.LoadQuotes?.LineID

if (libs.SharedLib.BatchUtils.isNewBatch()) {
    Set lineIDs = api.global.quotes?.collect()?.LineID?.toSet() ?: new HashSet<>()

    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("QuoteScales")

    def query = ctx.newQuery(ds, false)
            .select("LineID", "LineID")
            .select("ScaleQty", "ScaleQty")
            .select("ScaleUOM", "ScaleUOM")
            .select("Price", "Price")
            .select("PriceUOM", "PriceUOM")
            .setUseCache(false)
            .where(Filter.in("LineID", lineIDs))
            .orderBy("ScaleQty")

    api.global.quoteScales = ctx.executeQuery(query)?.getData()?.groupBy { it.LineID } ?: [:]
}

return api.global.quoteScales[lineID] ?: []