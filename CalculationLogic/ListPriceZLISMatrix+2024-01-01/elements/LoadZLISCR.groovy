//if (libs.SharedLib.BatchUtils.isNewBatch()) {
//    List fields = ["key2", "key3"]
//
//    List customFilters = [
//            Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A901"]),
//            Filter.equal("key1", "ZLIS"),
//            Filter.in("key2", api.global.salesOrgs),
//            Filter.in("key3", api.global.currentBatch),
//            Filter.lessOrEqual("validFrom", api.global.effectiveDate),
//            Filter.greaterOrEqual("validTo", api.global.effectiveDate),
//            Filter.isNull("attribute5")
//    ]
//
//    api.global.ZLISCR = api.stream("CRCI3", "-lastUpdateDate", fields, *customFilters)
//            ?.withCloseable {
//                it.collect().groupBy { [it.key2, it.key3] }
//            } ?: [:]
//}
//
//return api.global.ZLISCR