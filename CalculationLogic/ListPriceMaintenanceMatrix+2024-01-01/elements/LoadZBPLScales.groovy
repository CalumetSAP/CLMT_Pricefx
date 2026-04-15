if (libs.SharedLib.BatchUtils.isNewBatch() && out.LoadZBPL) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZBPLScales")

    def query = ctx.newQuery(dm, false)
            .select("ConditionRecordNo", "ConditionRecordNo")
            .select("LineNumber", "LineNumber")
            .select("lastUpdateDate", "lastUpdateDate")
            .where(Filter.in("ConditionRecordNo", out.LoadZBPL?.values()?.ConditionRecordNo?.unique()))


    api.global.ZBPLScales = ctx.executeQuery(query)?.getData()?.groupBy {
        it.ConditionRecordNo
    }?.collectEntries { conditionRecordNo, row ->
        [conditionRecordNo, [
                scaleLineNumbers: row*.LineNumber?.sort { it as Integer },
                lastUpdateDate  : row*.lastUpdateDate.max()
        ]]
    } ?: [:]
}

return api.global.ZBPLScales