String salesOrg = api.local.salesOrg
String material = api.local.material

// when the new batch starts, do pre-load ListPrice (DS) (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    List fields = ["key2", "key3", "validFrom", "validTo", "unitOfMeasure", "conditionValue", "currency", "lastUpdateDate"]

    List customFilters = [
            Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A901"]),
            Filter.equal("key1", "ZLIS"),
            Filter.in("key2", api.global.salesOrgs),
            Filter.in("key3", api.global.currentBatch),
            Filter.isNull("attribute5")
    ]

    if (api.global.noEffectiveDatesOverridden) {
        customFilters.add(Filter.lessOrEqual("validFrom", api.local.newEffectiveDate))
        customFilters.add(Filter.greaterOrEqual("validTo", api.local.newEffectiveDate))
    } else {
        customFilters.add(Filter.lessOrEqual("validFrom", api.global.maxNewEffectiveDate))
        customFilters.add(Filter.greaterOrEqual("validTo", api.global.minNewEffectiveDate))
    }

    api.global.ZLISCR = api.stream("CRCI3", "-lastUpdateDate", fields, *customFilters)
            ?.withCloseable {
                it.collect().groupBy { [it.key2, it.key3] }
            } ?: [:]
}

if (api.global.noEffectiveDatesOverridden) {
    return api.global.ZLISCR?.get([salesOrg, material])?.find() ?: [:]
} else {
    return api.global.ZLISCR?.get([salesOrg, material])?.find {
        it.validFrom <= api.local.newEffectiveDate && it.validTo >= api.local.newEffectiveDate
    } ?: [:]
}