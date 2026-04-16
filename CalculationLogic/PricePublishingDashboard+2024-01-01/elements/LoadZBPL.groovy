String pricingDate = out.Filters?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY)
def salesOrgs = api.local.quotes?.collect { it.SalesOrg }
def pricelists = api.local.quotes?.collect { it.PriceListPLT }
def materials = api.local.quotes?.collect { it.Material }

List customFilters = [
        Filter.in("SalesOrganization", salesOrgs),
        Filter.in("Pricelist", pricelists),
        Filter.in("Material", materials)
]

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource("ZBPL")

def query = ctx.newQuery(dm, false)
        .select("Material", "Material")
        .select("ValidFrom", "ValidFrom")
        .select("ValidTo", "ValidTo")
        .select("SalesOrganization", "SalesOrganization")
        .select("DistributionChannel", "DistributionChannel")
        .select("ConditionRecordNo", "ConditionRecordNo")
        .select("Pricelist", "Pricelist")
        .select("Amount", "Amount")
        .select("Per", "Per")
        .select("ScaleUoM", "ScaleUoM")
        .select("UnitOfMeasure", "UnitOfMeasure")
        .select("ConditionCurrency", "ConditionCurrency")
        .select("lastUpdateDate", "lastUpdateDate")
        .where(*customFilters)
        .orderBy("lastUpdateDate DESC")

api.global.zbpl = ctx.executeQuery(query)?.getData()?.groupBy {[it.SalesOrganization, it.Pricelist, it.Material] } ?: [:]