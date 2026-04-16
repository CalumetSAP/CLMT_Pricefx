List lineIds = api.local.lineIds
if (!lineIds) return []

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource("Quotes")

def query = ctx.newQuery(dm, false)
        .select("QuoteID", "QuoteID")
        .select("LineID", "LineID")
        .select("SalesOrg", "SalesOrg")
        .select("SAPContractNumber", "SAPContractNumber")
        .select("SAPLineID", "SAPLineID")
        .select("Material", "Material")
        .select("Division", "Division")
        .select("SoldTo", "SoldTo")
        .select("uuid", "uuid")
        .select("PriceValidFrom", "PriceValidFrom")
        .select("PriceValidTo", "PriceValidTo")
        .select("PricingUOM", "PricingUOM")
        .select("Price", "Price")
        .select("PriceType", "PriceType")
        .select("Per", "Per")
        .select("Currency", "Currency")
        .select("FreightEstimate", "FreightEstimate")
        .select("FreightAmount", "FreightAmount")
        .select("FreightUOM", "FreightUOM")
        .select("FreightValidFrom", "FreightValidFrom")
        .select("FreightValidto", "FreightValidto")
        .select("FreightTerm", "FreightTerm")
        .where(Filter.in("LineID", lineIds))

return ctx.executeQuery(query)?.getData() ?: []