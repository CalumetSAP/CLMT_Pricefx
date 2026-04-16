if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().conditionRecords("A904")
    def filters = [
            t1.ConditionType.in(["ZFDL", "ZFDD"]),
            qapi.exprs().or(
                    t1."Integration Flag".isNull(),
                    t1."Integration Flag".equal("Change")
            ),
            t1."Superseded Flag".isNull()
    ]
    if (api.global.salesOrgs) filters.add(t1.SalesOrg.in(api.global.salesOrgs))
    if (api.global.batchKeys1) filters.add(t1.Material.in(api.global.batchKeys1))
    if (api.global.effectiveDate) filters.add(t1.validTo().greaterOrEqual(qapi.exprs().dateOnly(api.global.effectiveDate as Date)))

    def filter = qapi.exprs().and(*filters)
    api.global.freightCR = qapi.source(t1, [t1.ContractNr, t1.ContractLine], filter).stream { it.collect {
        it.ContractNr + "|" + it.ContractLine
    } }
}

def freights = api.global.freightCR ? api.global.freightCR as List : []

return freights.contains(api.local.contractNumber + "|" + api.local.contractLine)