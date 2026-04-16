if (libs.SharedLib.BatchUtils.isNewBatch()) {
    List<String> lineIds = api.global.lineIds as List

    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("QuoteScales")

    def query = ctx.newQuery(ds, false)
            .select("LineID", "LineID")
            .select("ScaleQty", "ScaleQty")
            .select("ScaleUOM", "ScaleUOM")
            .select("Price", "Price")
            .select("PriceUOM", "PriceUOM")
            .setUseCache(false)
            .where(Filter.in("LineID", lineIds))
            .orderBy("ScaleQty")

    api.global.quoteScales = ctx.executeQuery(query)?.getData()?.groupBy { it.LineID } ?: [:]
}

String lineID = out.LoadQuotes?.LineID

return api.global.quoteScales[lineID] ?: []