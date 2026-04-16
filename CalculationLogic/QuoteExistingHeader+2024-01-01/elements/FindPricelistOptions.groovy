//if (api.isInputGenerationExecution() || !(api.local.lineItemPriceType.contains("2") || api.local.lineItemPriceType.contains("3"))) return [:]
if (api.isInputGenerationExecution()) return [:]

if (!api.local.lineItemPricingFilters) return [:]

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_PRICING)

def customFilter = Filter.or(*api.local.lineItemPricingFilters)
def query = ctx.newQuery(dm, false)
        .select("Material", "Material")
        .select("Pricelist", "PricelistID")
        .where(customFilter)

def result = ctx.executeQuery(query)

def pricelistNames = out.FindPricelist ?: [:]
def pricelistMap = [:]
result?.getData()?.each {
    if (!pricelistMap.containsKey(it.Material)) pricelistMap[it.Material] = new HashSet<String>()
    def pricelist = pricelistNames?.get(it.PricelistID) ?: it.PricelistID
    pricelistMap[it.Material].add(pricelist)
}

return pricelistMap ?: [:]