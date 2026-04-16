String pricingDate = out.Filters?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY)
def salesOrgs = api.local.quotes?.collect { it.SalesOrg }
def pricelists = api.local.quotes?.collect { it.PriceListPLT }
def materials = api.local.quotes?.collect { it.Material }

List fields = ["key2", "key3", "key4", "key5", "validFrom", "validTo", "unitOfMeasure", "priceUnit",
               "conditionValue", "currency", "attribute2", "attribute3", "attribute4", "attribute5", "lastUpdateDate"]

List customFilters = [
        Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A932"]),
        Filter.equal("key1", "ZBPL"),
        Filter.in("key2", salesOrgs),
        Filter.in("key4", pricelists),
        Filter.in("key5", materials),
]

if (pricingDate) {
    customFilters.add(Filter.lessOrEqual("validFrom", pricingDate))
    customFilters.add(Filter.greaterOrEqual("validTo", pricingDate))
}

api.global.zbplCR = api.stream("CRCI5", "-lastUpdateDate", fields, *customFilters)
        ?.withCloseable {
            it.collect().groupBy { [it.key2, it.key4, it.key5] }
        } ?: [:]