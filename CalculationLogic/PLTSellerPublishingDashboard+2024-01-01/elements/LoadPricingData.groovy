final constants = libs.DashboardConstantsLibrary.PLTDashboard

def dmCtx = api.getDatamartContext()
def ds = dmCtx.getDataSource("BasePricing")
def query = dmCtx.newQuery(ds, false)

def configurator = out.Filters
String pricingDate = configurator?.get(constants.PRICING_DATE_INPUT_KEY)
def pricelist = configurator?.get(constants.PRICELIST_INPUT_KEY)
def materials = api.global.materials

query.select("EffectiveDate", "EffectiveDate")
query.select("ProductId", "ProductId")
query.select("PricelistID", "PricelistID")
query.select("MOQ", "MOQ")
query.select("MOQUOM", "MOQUOM")

if (pricingDate) {
    query.where(
            Filter.and(
                    Filter.lessOrEqual("EffectiveDate", pricingDate),
                    Filter.greaterOrEqual("ExpirationDate", pricingDate)
            )
    )
}

query.where(Filter.equal("PricelistID", pricelist))
query.where(Filter.in("ProductId", materials))

query.orderBy("EffectiveDate DESC")

def result = [:]
def key
dmCtx.executeQuery(query)?.getData()?.each{ it ->
    key = it.ProductId + "|" + it.PricelistID
    result.putIfAbsent(key, it)
}

api.global.pricingData = result

return null