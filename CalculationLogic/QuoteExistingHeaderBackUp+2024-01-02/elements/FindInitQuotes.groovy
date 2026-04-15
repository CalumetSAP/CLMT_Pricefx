if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

if (!api.local.addedContracts) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def basePricingFilters = []

def contracts = out.FindContractDSData
contracts?.each { contractNumber, lines ->
    lines?.each { line ->
        def priceType = line?.PriceType
        if (priceType == "2") {
            basePricingFilters.add(Filter.and(
                    Filter.lessOrEqual("PriceValidFrom", line?.PriceValidFrom),
                    Filter.greaterOrEqual("PriceValidTo", line?.PriceValidFrom),
                    Filter.equal("QuoteID", line?.QuoteID),
                    Filter.equal("LineID", line?.LineID),
                    Filter.equal("Material", line?.Material)
            ))
        }
    }
}

if (!basePricingFilters) return

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_QUOTES)

def customFilter = Filter.or(*basePricingFilters)

def query = ctx.newQuery(dm, false)
        .select("QuoteID", "QuoteID")
        .select("LineID", "LineID")
        .select("Price", "BasePrice")
        .select("PricingUOM", "UOM")
        .where(customFilter)

def result = ctx.executeQuery(query)
def basePricingMap = [:]
result?.getData()?.each {
    basePricingMap.putIfAbsent(it.QuoteID + "|" + it.LineID, it)
}

return basePricingMap