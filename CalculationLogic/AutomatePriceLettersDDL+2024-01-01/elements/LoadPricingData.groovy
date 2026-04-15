if (api.isInputGenerationExecution()) return

def calcItem = dist.calcItem
def dashboard = api.isDebugMode() ? "PB" : calcItem?.Value?.dashboard

if (dashboard != "PB") return

def dmCtx = api.getDatamartContext()
def ds = dmCtx.getDataSource("BasePricing")
def query = dmCtx.newQuery(ds, false)

def products = api.global.materialList ?: []
def pricelists = api.global.pricelistList ?: []

query.select("EffectiveDate", "EffectiveDate")
query.select("ProductId", "ProductId")
query.select("PricelistID", "PricelistID")
query.select("JobberPrice", "JobberPrice")
query.select("SRP", "SRP")
query.select("MAP", "MAP")

query.where(Filter.isNotNull("EffectiveDate"))
query.where(Filter.isNotNull("ExpirationDate"))
query.where(Filter.in("ProductId", products))
query.where(Filter.in("PricelistID", pricelists))

query.orderBy("EffectiveDate DESC")

api.global.pricingData = dmCtx.executeQuery(query)?.getData()?.groupBy { [it.ProductId, it.PricelistID] }

return null