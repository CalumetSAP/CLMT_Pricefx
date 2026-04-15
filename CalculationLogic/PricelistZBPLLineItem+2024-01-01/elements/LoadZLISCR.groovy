String salesOrg = api.local.salesOrg
String material = api.local.material

// when the new batch starts, do pre-load ListPrice (DS) (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().conditionRecords("A901")
    def fields = [
            t1.key2(),
            t1.key3(),
            t1.validFrom(),
            t1.validTo(),
            t1.unitOfMeasure(),
            t1.conditionValue(),
            t1.currency(),
            t1.lastUpdateDate(),
    ]

    def dateFilter = []
    if (api.global.noEffectiveDatesOverridden) {
        dateFilter.add(t1.validFrom().lessOrEqual(qapi.exprs().dateOnly(api.local.newEffectiveDate as Date)))
        dateFilter.add(t1.validTo().greaterOrEqual(qapi.exprs().dateOnly(api.local.newEffectiveDate as Date)))
    } else {
        if (api.global.maxNewEffectiveDate) {
            dateFilter.add(t1.validFrom().lessOrEqual(qapi.exprs().dateOnly(api.global.maxNewEffectiveDate as Date)))
        } else {
            dateFilter.add(t1.validFrom().lessOrEqual(qapi.exprs().dateOnly(api.local.newEffectiveDate as Date)))
        }
        if (api.global.minNewEffectiveDate) {
            dateFilter.add(t1.validTo().greaterOrEqual(qapi.exprs().dateOnly(api.global.minNewEffectiveDate as Date)))
        } else {
            dateFilter.add(t1.validTo().greaterOrEqual(qapi.exprs().dateOnly(api.local.newEffectiveDate as Date)))
        }
    }

    def filters = qapi.exprs().and(
            t1.key1().equal("ZLIS"),
            t1.key2().in(api.global.salesOrgs as List),
            t1.key3().in(api.global.currentBatch as List),
            qapi.exprs().or(
                    t1."Integration Flag".isNull(),
                    t1."Integration Flag".equal("Change")
            ),
            t1."Superseded Flag".isNull(),
            *dateFilter
    )

    api.global.ZLISCR = qapi.source(t1, fields, filters)
            .sortBy {cols -> [qapi.orders().descNullsLast(cols.lastUpdateDate)]}
            .stream { it.collect().groupBy { [it.key2, it.key3] } }
}

if (api.global.noEffectiveDatesOverridden) {
    return api.global.ZLISCR?.get([salesOrg, material])?.find() ?: [:]
} else {
    return api.global.ZLISCR?.get([salesOrg, material])?.find {
        it.validFrom <= api.local.newEffectiveDate && it.validTo >= api.local.newEffectiveDate
    } ?: [:]
}