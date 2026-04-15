String conditionRecordNo = out.LoadZBPL?.ConditionRecordNo
String lineNumber = api.local.lineNumber

if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.ZBPL) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZBPLScales")

    def query = ctx.newQuery(dm, false)
            .select("ConditionRecordNo", "ConditionRecordNo")
            .select("LineNumber", "LineNumber")
            .select("ScaleQuantity", "ScaleQuantity")
            .select("ConditionRate", "ConditionRate")
            .select("lastUpdateDate", "lastUpdateDate")
            .where(Filter.in("ConditionRecordNo", api.global.ZBPL?.values()?.ConditionRecordNo?.flatten()?.unique()))

    api.global.ZBPLScales = ctx.executeQuery(query)?.getData()?.
            groupBy {[it.ConditionRecordNo, it.LineNumber] }?.
            collectEntries { [(it.key): it.value.find()] } ?: [:]
}

if (conditionRecordNo && lineNumber) {
    return api.global.ZBPLScales?.get([conditionRecordNo, lineNumber]) ?: [:]
} else {
    return [:]
}