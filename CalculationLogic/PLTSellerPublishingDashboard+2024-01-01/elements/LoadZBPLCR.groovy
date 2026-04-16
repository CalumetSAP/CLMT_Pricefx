final constants = libs.DashboardConstantsLibrary.PLTDashboard

def configurator = out.Filters
def pricelist = configurator?.get(constants.PRICELIST_INPUT_KEY)
def materials = api.local.selectedMaterials

List fields = ["key2", "key3", "key4", "key5", "validFrom", "validTo", "unitOfMeasure", "priceUnit",
               "conditionValue", "currency", "attribute2", "attribute3", "attribute4", "attribute5", "lastUpdateDate"]

List customFilters = [
        Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A932"]),
        Filter.equal("key1", "ZBPL"),
        Filter.equal("key4", pricelist),
]

if (materials) customFilters.add(Filter.in("key5", materials))

api.global.zbplCR = api.stream("CRCI5", "-lastUpdateDate", fields, *customFilters)
        ?.withCloseable {
            it.collect().groupBy { [it.key4, it.key5] }
        } ?: [:]

return null