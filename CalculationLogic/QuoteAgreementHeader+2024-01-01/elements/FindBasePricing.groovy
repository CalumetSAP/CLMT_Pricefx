if (!quoteProcessor.isPostPhase()) return
if (!api.local.pricelists) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def materials = api.local.skusWithPL

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_PRICING)

def effectiveDate = quoteProcessor.getQuoteView().targetDate
def expiryDate = quoteProcessor.getQuoteView().expiryDate

def customFilter = Filter.and(
        Filter.lessOrEqual("EffectiveDate", effectiveDate),
        Filter.greaterOrEqual("ExpirationDate", expiryDate),
        Filter.in("ProductId", materials),
        Filter.in("PricelistID", api.local.pricelists)
)

def query = ctx.newQuery(dm, false)
        .select("ProductId", "ProductId")
        .select("PricelistID", "PricelistID")
        .select("BasePrice", "BasePrice")
        .select("UOM", "UOM")
        .select("EffectiveDate", "EffectiveDate")
        .select("ExpirationDate", "ExpirationDate")
        .where(customFilter)
        .orderBy("EffectiveDate DESC", "ExpirationDate DESC")

def result = ctx.executeQuery(query)
def data = result?.getData()
def pricingMap = [:]

data?.each {
    pricingMap.putIfAbsent(it.ProductId, it)
}

return pricingMap
