import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.matrixFreightCR) {
    List selectedConditionTypes = api.global.conditionType as List
    def finalRows = []
    def contractCombinationKeys = []

    def qapi = api.queryApi()

    def t1 = qapi.tables().conditionRecords("A904")
    def conditionTypeList = conditionTypeFilter(selectedConditionTypes, ["ZCSP", "ZPFX"])
    if (conditionTypeList != null) {
        def fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        def freightCR = api.global.matrixFreightCR
        def contractNumbers = freightCR?.collect { it.ContractNr }?.toSet()?.toList()
        def contractLines = freightCR?.collect { it.ContractLine }?.toSet()?.toList()
        def combinationKeys = freightCR?.collect { it.ContractNr + "|" + it.ContractLine }
        def combinationKeysWithValidFrom = freightCR?.collect { it.ContractNr + "|" + it.ContractLine + "|" + it.validFrom.format(fmt) }
        contractCombinationKeys = combinationKeysWithValidFrom

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
        if (api.global.salesOrgs) filters.add(t1.SalesOrg.in(api.global.salesOrgs))
        if (api.global.batchKeys1) filters.add(t1.Material.in(api.global.batchKeys1))
        if (api.global.effectiveDate) filters.add(t1.validTo().greaterOrEqual(qapi.exprs().dateOnly(api.global.effectiveDate as Date)))

        def filter = qapi.exprs().and(*filters)

        def rows = qapi.source(t1, [t1.Material, t1.ConditionType, t1.ContractNr, t1.ContractLine, t1.validFrom()], filter).stream { it.collect() }

        finalRows.addAll(rows.findAll { combinationKeys.contains(it.ContractNr + "|" + it.ContractLine) }.toList())
    }

    def eff = api.global.effectiveDate ? (api.global.effectiveDate as Date) : new Date()

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

    def foundKeys = []
    finalRows.groupBy { it.ContractNr + "|" + it.ContractLine }
            .each { key, rows2 ->
                if (rows2.any { sdf.parse(it.validFrom.toString()) <= eff }) foundKeys.add(key + "|CURRENT")
                if (rows2.any { sdf.parse(it.validFrom.toString()) > eff }) foundKeys.add(key + "|FUTURE")
            }

    api.global.matrixContractCombinationKeys = contractCombinationKeys
    api.global.matrixConditionRecords = finalRows
    api.global.matrixConditionRecordsFound = foundKeys
    api.global.matrixConditionRecordsByMaterial = (finalRows ?: []).groupBy { it.Material }
}

return null

def conditionTypeFilter(List selectedConditionTypes, List possibleConditionTypes) {
    if (!selectedConditionTypes) return []

    def intersect = selectedConditionTypes.intersect(possibleConditionTypes).toList()

    return intersect ?: null
}