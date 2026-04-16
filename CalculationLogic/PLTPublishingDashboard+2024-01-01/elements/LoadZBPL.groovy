final constants = libs.DashboardConstantsLibrary.PLTDashboard

def configurator = out.Filters
def pricelist = configurator?.get(constants.PRICELIST_INPUT_KEY)
def materials = api.local.selectedMaterials

List customFilters = [
        Filter.equal("Pricelist", pricelist)
]

if (materials) customFilters.add(Filter.in("Material", materials))

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

api.global.zbpl = ctx.executeQuery(query)?.getData()?.groupBy {[it.Pricelist, it.Material] } ?: [:]

return null