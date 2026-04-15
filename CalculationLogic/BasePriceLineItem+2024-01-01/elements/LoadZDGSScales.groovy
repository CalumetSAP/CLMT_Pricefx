// when the new batch starts, do pre-load ZDGSScales (DS) (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZDGSScales")

    def customFilter = Filter.and(
            Filter.in("CondRecNo", api.global.ZDGS?.values()?.collectMany { it.keySet() }?.flatten())
    )

    def query = ctx.newQuery(dm, false)
            .select("CondRecNo", "CondRecNo")
            .select("SeqCondRecNo", "SeqCondRecNo")
            .select("LineNumber", "LineNumber")
            .select("ScaleQuantity", "ScaleQuantity")
            .select("ConditionRate", "ConditionRate")
            .where(customFilter)

    def result = ctx.executeQuery(query)
    def data = result?.getData()
    def newZDGS = [:]

    api.global.ZDGS.each { key, condRecNos ->
        newZDGS[key] = [:]
        condRecNos.each { condRecNo, records ->
            records.each { record ->
                data?.findAll { it.CondRecNo == condRecNo }.each { scaleRecord ->
                    def newRecord = [:]
                    newRecord.putAll(record)
                    newRecord.ScaleQuantity = scaleRecord.ScaleQuantity
                    newRecord.ConditionRate = scaleRecord.ConditionRate
                    if (!newZDGS[key].containsKey(condRecNo)) {
                        newZDGS[key][condRecNo] = []
                    }
                    newZDGS[key][condRecNo] << newRecord
                }
            }
        }
    }

    api.global.ZDGS = newZDGS
}
