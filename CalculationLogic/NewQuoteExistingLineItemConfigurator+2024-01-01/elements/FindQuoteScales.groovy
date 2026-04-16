//if (api.isInputGenerationExecution() || !InputPricelist?.input?.getValue() || InputPriceType.input?.getValue() != "2") return
//
//final tablesConstants = libs.QuoteConstantsLibrary.Tables
//
//def ctx = api.getDatamartContext()
//def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_QUOTES_SCALES)
//
//def quoteID = api.local.contractData?.QuoteID as String
//def lineID = api.local.contractData?.LineId as String
//
//def customFilter = Filter.and(
//        Filter.equal("QuoteID", quoteID),
//        Filter.equal("LineID", lineID),
//)
//
//def query = ctx.newQuery(dm, false)
//        .select("QuoteID", "QuoteID")
//        .select("LineID", "LineID")
//        .select("ScaleQty", "ScaleQuantity")
//        .select("ScaleUOM", "ScaleUOM")
//        .select("Price", "ConditionRate")
//        .select("PriceUOM", "PriceUOM")
//        .where(customFilter)
//
//def result = ctx.executeQuery(query)
//return [(quoteID + "|" + lineID): result?.getData()?.toList()]