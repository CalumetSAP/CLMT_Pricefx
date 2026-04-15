if (!quoteProcessor.isPostPhase()) return
if (!api.local.pricelists) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def materials = api.local.skusWithPL

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_ZDGS)

def effectiveDate = quoteProcessor.getQuoteView().targetDate
def expiryDate = quoteProcessor.getQuoteView().expiryDate

def customFilter = Filter.and(
        Filter.lessOrEqual("ValidFrom", effectiveDate),
        Filter.greaterOrEqual("ValidTo", expiryDate),
        Filter.in("Material", materials),
        Filter.in("PL", api.local.pricelists as List)
)

def query = ctx.newQuery(dm, false)
        .select("Material", "Material")
        .select("PL", "PricelistID")
        .select("CondRecNo", "CondRecNo")
        .select("ValidFrom", "ValidFrom")
        .select("ValidTo", "ValidTo")
        .where(customFilter)
        .orderBy("ValidFrom DESC", "ValidTo DESC")

def result = ctx.executeQuery(query)
def data = result?.getData()
def zdgsMap = [:]

data?.each {
    zdgsMap.putIfAbsent(it.Material, it)
}

return zdgsMap
