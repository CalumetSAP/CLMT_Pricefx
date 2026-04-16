String salesOrg = api.local.salesOrg
String pricelist = api.local.pricelist
String material = api.local.material

if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.pricelists) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().conditionRecords("A932")
    def fields = [
            t1.key2(),
            t1.key3(),
            t1.key4(),
            t1.key5(),
            t1.validFrom(),
            t1.validTo(),
            t1.unitOfMeasure(),
            t1.priceUnit(),
            t1.conditionValue(),
            t1.currency(),
            t1.lastUpdateDate(),
            t1.Scales.as("attribute2"),
            t1.ScaleUOM.as("attribute3"),
            t1."Integration Flag".as("attribute4"),
            t1."Superseded Flag".as("attribute5")
    ]

    def filters = qapi.exprs().and(
            t1.key1().equal("ZBPL"),
            t1.key2().in(api.global.salesOrgs as List),
            t1.key4().in(api.global.pricelists as List),
            t1.key5().in(api.global.currentBatch as List),
    )

    api.global.ZBPLCR = qapi.source(t1, fields, filters)
            .sortBy {cols -> [qapi.orders().descNullsLast(cols.lastUpdateDate)]}
            .stream { it.collect().groupBy { [it.key2, it.key4, it.key5] } }
}

//if (api.global.noEffectiveDatesOverridden) {
//    return api.global.ZBPLCR?.get([salesOrg, pricelist, material])?.find() ?: [:]
//} else {
//    return api.global.ZBPLCR?.get([salesOrg, pricelist, material])?.find {
//        it.validFrom <= api.local.newEffectiveDate && it.validTo >= api.local.newEffectiveDate
//    } ?: [:]
//}
return api.global.ZBPLCR?.get([salesOrg, pricelist, material])