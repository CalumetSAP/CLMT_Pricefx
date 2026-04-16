if (api.isInputGenerationExecution()) return

def plId = api.isDebugMode() ? "695" : dist?.calcItem?.Key2

def plItems = libs.PricelistLib.Common.getAllPLItems(plId)
def materials = plItems?.collect {it.sku }?.unique()
def pricelists = plItems?.collect {it.key2?.split('-')?.getAt(1) }?.unique()

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource("BasePricing")

def customFilter = Filter.and(
        Filter.in("ProductId", materials),
        Filter.in("PricelistID", pricelists),
)

def query = ctx.newQuery(dm, false)
        .select("EffectiveDate", "EffectiveDate")
        .select("ExpirationDate", "ExpirationDate")
        .select("ProductId", "ProductId")
        .select("PricelistID", "PricelistID")
        .select("MOQ", "MOQ")
        .select("MOQUOM", "MOQUOM")
        .setUseCache(false)
        .where(customFilter)
        .orderBy("lastUpdateDate DESC")

api.global.pricingMap = ctx.executeQuery(query)?.getData()?.groupBy { [it.ProductId, it.PricelistID] } ?: [:]

return null