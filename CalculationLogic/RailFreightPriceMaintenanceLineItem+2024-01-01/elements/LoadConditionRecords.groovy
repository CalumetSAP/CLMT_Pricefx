import java.time.format.DateTimeFormatter

if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.batchKeys2) {
    def fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    List selectedConditionTypes = api.global.conditionType as List

    def finalRows = []

    def qapi = api.queryApi()

    def t1 = qapi.tables().conditionRecords("A904")
    def conditionTypeList = conditionTypeFilter(selectedConditionTypes, ["ZCSP", "ZPFX"])
    if (conditionTypeList != null) {
        def secondaryKeys = api.global.batchKeys2 as List
        def contractNumbers = secondaryKeys?.collect { it.split("-")[0] }?.toSet()?.toList()
        def contractLines = secondaryKeys?.collect { it.split("-")[1] }?.toSet()?.toList()

        def splitSK
        def combinationKeys = secondaryKeys?.collect {
            splitSK = it.split("-")
            return splitSK[0] + "|" + splitSK[1]
        }

        def filters = [
                t1.ContractNr.in(contractNumbers),
                t1.ContractLine.in(contractLines),
                t1.ConditionType.notIn(["ZFDL", "ZFDD"]),
                qapi.exprs().or(
                        t1."Integration Flag".isNull(),
                        t1."Integration Flag".equal("Change")
                ),
                t1."Superseded Flag".isNull()
        ]

        if (conditionTypeList) filters.add(t1.ConditionType.in(conditionTypeList as List))
        if (api.global.salesOrgs) filters.add(t1.SalesOrg.in(api.global.salesOrgs as List))
        if (api.global.effectiveDate) filters.add(t1.validTo().greaterOrEqual(qapi.exprs().dateOnly(api.global.effectiveDate as Date)))

        def fields = [t1.Material, t1.ConditionType, t1.ContractNr, t1.ContractLine, t1.validFrom(), t1.validTo(), t1.conditionValue(),
                      t1.currency(), t1.unitOfMeasure(), t1.Scales, t1.ScaleUOM]
        def filter = qapi.exprs().and(*filters)

        def rows = qapi.source(t1, fields, filter).stream { it.collect() }

        finalRows.addAll(rows.findAll { combinationKeys.contains(it.ContractNr + "|" + it.ContractLine) }.toList())
    }

    api.global.conditionRecords = finalRows.collectEntries {
        [(it.ContractNr + "|" + it.ContractLine + "|" + it.validFrom.format(fmt)): it]
    }
}

return api.global.conditionRecords[api.local.contractNumber + "|" + api.local.contractLine + "|" + api.local.validFrom] ?: [:]

def conditionTypeFilter(List selectedConditionTypes, List possibleConditionTypes) {
    if (!selectedConditionTypes) return []

    def intersect = selectedConditionTypes.intersect(possibleConditionTypes).toList()

    return intersect ?: null
}