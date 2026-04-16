def dmCtx = api.getDatamartContext()
def ds = dmCtx.getDataSource("BasePricing")
def query = dmCtx.newQuery(ds, false)

def configurator = out.Filters

String pricingDate = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY)
def products = api.local.allRows?.collect { it.Material }?.unique() ?: []
def pricelists = api.local.allRows?.collect { it.PriceListPLT }?.unique()

query.select("EffectiveDate", "EffectiveDate")
query.select("ProductId", "ProductId")
query.select("PricelistID", "PricelistID")
query.select("JobberPrice", "JobberPrice")
query.select("SRP", "SRP")
query.select("MAP", "MAP")

if (pricingDate) {
    query.where(
            Filter.and(
                    Filter.lessOrEqual("EffectiveDate", pricingDate),
                    Filter.greaterOrEqual("ExpirationDate", pricingDate)
            )
    )
}

query.where(Filter.in("ProductId", products))
query.where(Filter.in("PricelistID", pricelists))

query.orderBy("EffectiveDate DESC")

def result = [:]
def key
dmCtx.executeQuery(query)?.getData()?.each{ it ->
    key = it.ProductId + "|" + it.PricelistID
    result.putIfAbsent(key, it)
}

api.global.pricingData = result