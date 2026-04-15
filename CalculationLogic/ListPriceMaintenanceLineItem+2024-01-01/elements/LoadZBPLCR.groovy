String salesOrg = api.local.salesOrg
String pricelist = api.local.pricelist
String material = api.local.material

if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.pricelists) {
    List fields = ["key2", "key3", "key4", "key5", "validFrom", "validTo", "unitOfMeasure", "priceUnit",
                   "conditionValue", "currency", "attribute2", "attribute3", "lastUpdateDate"]

    List customFilters = [
            Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A932"]),
            Filter.equal("key1", "ZBPL"),
            Filter.in("key2", api.global.salesOrgs),
            Filter.in("key4", api.global.pricelists),
            Filter.in("key5", api.global.currentBatch)
    ]

    if (api.global.noEffectiveDatesOverridden) {
        customFilters.add(Filter.lessOrEqual("validFrom", api.local.newEffectiveDate))
        customFilters.add(Filter.greaterOrEqual("validTo", api.local.newEffectiveDate))
    } else {
        customFilters.add(Filter.lessOrEqual("validFrom", api.global.maxNewEffectiveDate))
        customFilters.add(Filter.greaterOrEqual("validTo", api.global.minNewEffectiveDate))
    }

    api.global.ZBPLCR = api.stream("CRCI5", "-lastUpdateDate", fields, *customFilters)
            ?.withCloseable {
                it.collect().groupBy { [it.key2, it.key4, it.key5] }
            } ?: [:]
}

if (api.global.noEffectiveDatesOverridden) {
    return api.global.ZBPLCR?.get([salesOrg, pricelist, material])?.find() ?: [:]
} else {
    return api.global.ZBPLCR?.get([salesOrg, pricelist, material])?.find {
        it.validFrom <= api.local.newEffectiveDate && it.validTo >= api.local.newEffectiveDate
    } ?: [:]
}