String conditionRecordNo = out.LoadZBPL?.ConditionRecordNo

if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.ZBPL) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZBPLScales")

    def query = ctx.newQuery(dm, false)
            .select("ConditionRecordNo", "ConditionRecordNo")
            .select("ScaleQuantity", "ScaleQuantity")
            .select("ConditionRate", "ConditionRate")
            .select("lastUpdateDate", "lastUpdateDate")
            .where(Filter.in("ConditionRecordNo", api.global.ZBPL?.values()?.ConditionRecordNo?.flatten()?.unique()))
            .orderBy("ScaleQuantity")

    api.global.ZBPLScales = ctx.executeQuery(query)?.getData()?.
            groupBy {it.ConditionRecordNo }?: [:]
}

if (conditionRecordNo) {
    return api.global.ZBPLScales?.get(conditionRecordNo) ?: []
} else {
    return []
}