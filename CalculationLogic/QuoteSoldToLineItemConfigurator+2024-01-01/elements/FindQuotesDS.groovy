if (api.isInputGenerationExecution() || !InputPricelist?.input?.getValue() || InputPriceType.input?.getValue() != "2") return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_QUOTES)

def quoteID = api.local.contractData?.QuoteID as String
def lineID = api.local.contractData?.LineId as String
def sku = InputMaterial?.input?.getValue()
def effectiveDate = InputPriceValidFrom?.input?.getValue()
def expiryDate = InputPriceValidTo?.input?.getValue()
def pricelist = InputPricelist?.input?.getValue()?.split(" - ")?.getAt(0)

def customFilter = Filter.and(
        Filter.equal("QuoteID", quoteID),
        Filter.equal("LineID", lineID),
        Filter.lessOrEqual("PriceValidFrom", effectiveDate),
        Filter.greaterOrEqual("PriceValidTo", effectiveDate),
        Filter.equal("Material", sku),
        Filter.equal("PriceListPLT", pricelist)
)

def query = ctx.newQuery(dm, false)
        .select("QuoteID", "QuoteID")
        .select("LineID", "LineID")
        .select("Price", "BasePrice")
        .select("PricingUOM", "UOM")
        .where(customFilter)

def result = ctx.executeQuery(query)
return data = result?.getData()?.find()