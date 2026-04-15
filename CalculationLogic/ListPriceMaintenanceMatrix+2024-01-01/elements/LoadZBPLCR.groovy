if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.pricelists) {
    List fields = ["key2", "key4", "key5", "attribute2", "lastUpdateDate"]

    List customFilters = [
            Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A932"]),
            Filter.equal("key1", "ZBPL"),
            Filter.in("key2", api.global.salesOrgs),
            Filter.in("key4", api.global.pricelists),
            Filter.in("key5", api.global.currentBatch),
            Filter.lessOrEqual("validFrom", api.global.effectiveDate),
            Filter.greaterOrEqual("validTo", api.global.effectiveDate)
    ]

    api.global.ZBPLCR = api.stream("CRCI5", "-lastUpdateDate", fields, *customFilters)
            ?.withCloseable {
                it.collect()
                        .groupBy { [it.key2, it.key4, it.key5] }
                        .collectEntries {[(it.key): it.value.find()]}
            } ?: [:]
}

return api.global.ZBPLCR