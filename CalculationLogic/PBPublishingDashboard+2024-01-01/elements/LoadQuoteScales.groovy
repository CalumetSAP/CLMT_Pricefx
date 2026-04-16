final query = libs.QuoteLibrary.Query

List<String> lineIds = api.local.allRows?.LineID?.unique() ?: []

Map quoteScales = [:]
lineIds.collate(2000).each { lineIdsBatch ->
    quoteScales.putAll(query.getQuoteScalesRows(lineIdsBatch).groupBy { it.LineID })
}

api.local.quoteScales = quoteScales