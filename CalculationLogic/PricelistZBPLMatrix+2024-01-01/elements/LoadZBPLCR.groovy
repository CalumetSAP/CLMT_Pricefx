if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.pricelists) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().conditionRecords("A932")
    def fields = [
            t1.key2(),
            t1.key4(),
            t1.key5(),
            t1.validFrom(),
            t1.validTo(),
            t1.lastUpdateDate(),
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

return api.global.ZBPLCR