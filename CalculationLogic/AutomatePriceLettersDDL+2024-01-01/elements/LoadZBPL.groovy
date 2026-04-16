if (api.isInputGenerationExecution()) return

def salesOrgs = api.global.salesOrgList
def pricelists = api.global.pricelistList
def materials = api.global.materialList

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

return null