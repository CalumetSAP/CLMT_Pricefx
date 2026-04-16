if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !out.FindQuotesDS) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_QUOTES_SCALES)

def quoteID = out.FindQuotesDS?.values()?.toList()?.collect { it.QuoteID } as List
def lineID = out.FindQuotesDS?.values()?.toList()?.collect { it.LineID } as List

def customFilter = Filter.and(
        Filter.in("QuoteID", quoteID),
        Filter.in("LineID", lineID),
)

def query = ctx.newQuery(dm, false)
        .select("QuoteID", "QuoteID")
        .select("LineID", "LineID")
        .select("ScaleQty", "ScaleQuantity")
        .select("ScaleUOM", "ScaleUOM")
        .select("Price", "ConditionRate")
        .select("PriceUOM", "PriceUOM")
        .where(customFilter)

def result = ctx.executeQuery(query)
def data = result?.getData()
def scalesMap = [:]

data?.each {
    def key = it.QuoteID + "|" + it.LineID
    if (!scalesMap.containsKey(key)) scalesMap[key] = []
    scalesMap[key].add(it)
}

return scalesMap