if (api.isInputGenerationExecution()) return

def data = api.jsonDecode(filterFormulaParam)

def pricelist = data.Pricelist
def pricingDate = data.PricingDate

List fields = ["key5"]

List customFilters = [
        Filter.equal("conditionRecordSetId", api.local.conditionRecordSetMap["A932"]),
        Filter.equal("key1", "ZBPL")
]

if (pricingDate) {
    customFilters.add(Filter.lessOrEqual("validFrom", pricingDate))
    customFilters.add(Filter.greaterOrEqual("validTo", pricingDate))
}
if (pricelist) customFilters.add(Filter.equal("key4", pricelist))

api.local.zbplCRMaterial = api.stream("CRCI5", "-lastUpdateDate", fields, *customFilters)
        ?.withCloseable {
            it.collect{ it.key5 }
        } ?: []

return null