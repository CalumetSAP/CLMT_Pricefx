if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

if (!api.local.addedContracts) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def basePricingFilters = []

def contracts = out.FindContractDSData
contracts?.each { contractNumber, lines ->
    lines?.each { line ->
        def pricelist = line?.PriceListPLT
        if (pricelist) {
            basePricingFilters.add(Filter.and(
                    Filter.lessOrEqual("ValidFrom", line?.PriceValidFrom),
                    Filter.greaterOrEqual("ValidTo", line?.PriceValidFrom),
                    Filter.equal("Material", line?.Material),
                    Filter.equal("Pricelist", pricelist)
            ))
        }
    }
}

if (!basePricingFilters) return

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_PRICING)

def customFilter = Filter.or(*basePricingFilters)

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