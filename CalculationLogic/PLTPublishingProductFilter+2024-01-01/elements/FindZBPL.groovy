if (api.isInputGenerationExecution()) return

def data = api.jsonDecode(filterFormulaParam)

def pricelist = data.Pricelist
def pricingDate = data.PricingDate

List customFilters = []

if (pricingDate) {
    customFilters.add(Filter.lessOrEqual("ValidFrom", pricingDate))
    customFilters.add(Filter.greaterOrEqual("ValidTo", pricingDate))
}
if (pricelist) customFilters.add(Filter.equal("Pricelist", pricelist))

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource("ZBPL")

def query = ctx.newQuery(dm, false)
        .select("Material", "Material")
        .where(*customFilters)

api.local.zbplMaterial = ctx.executeQuery(query)?.getData()?.collect { it.Material } ?: []

return null