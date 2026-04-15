if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.basePricingFilters) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_PRICING)

def customFilter = Filter.or(*api.local.basePricingFilters)

def query = ctx.newQuery(dm, false)
        .select("Material", "ProductId")
        .select("ValidFrom", "ValidFrom")
        .select("ValidTo", "ValidTo")
        .select("SalesOrganization", "SalesOrganization")
        .select("ConditionRecordNo", "ConditionRecordNo")
        .select("Pricelist", "PricelistID")
        .select("Amount", "BasePrice")
        .select("Per", "Per")
        .select("UnitOfMeasure", "UOM")
        .select("ScaleUoM", "ScaleUOM")
        .select("lastUpdateDate", "lastUpdateDate")
        .where(customFilter)
        .orderBy("lastUpdateDate DESC")

def result = ctx.executeQuery(query)
def basePricingMap = [:]
result?.getData()?.each {
    basePricingMap.putIfAbsent(it.SalesOrganization + "|" + it.PricelistID + "|" + it.ProductId, it)
}

return basePricingMap