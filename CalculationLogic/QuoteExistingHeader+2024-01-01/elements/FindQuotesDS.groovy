if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.quotesPricingFilters) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_QUOTES)

def customFilter = Filter.or(*api.local.quotesPricingFilters)

def query = ctx.newQuery(dm, false)
        .select("QuoteID", "QuoteID")
        .select("LineID", "LineID")
        .select("Price", "BasePrice")
        .select("PricingUOM", "UOM")
        .select("PriceListPLT", "PriceListPLT")
        .where(customFilter)

def result = ctx.executeQuery(query)
def mapForScales = [:]
def basePricingMap = [:]
result?.getData()?.each {
    mapForScales.putIfAbsent(it.QuoteID + "|" + it.LineID, it)
    if (it.PriceListPLT) basePricingMap.putIfAbsent(it.QuoteID + "|" + it.LineID + "|" + it.PriceListPLT, it)
}
api.local.basePricingMap = basePricingMap
return mapForScales