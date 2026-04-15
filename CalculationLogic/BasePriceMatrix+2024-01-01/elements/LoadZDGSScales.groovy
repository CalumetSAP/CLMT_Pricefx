if (api.isInputGenerationExecution()) api.abortCalculation()

if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZDGSScales")

    def customFilter = Filter.and(
            Filter.in("CondRecNo", api.global.ZDGS?.values()?.collectMany { it.keySet() }?.flatten())
    )

    def query = ctx.newQuery(dm, false)
            .select("CondRecNo", "CondRecNo")
            .select("ScaleQuantity", "ScaleQuantity")
            .where(customFilter)

    def result = ctx.executeQuery(query)
    def data = result?.getData()?.collect { it }
    def newZDGS = [:]

    api.global.ZDGS.each { key, condRecNos ->
        newZDGS[key] = [:]
        condRecNos.each { condRecNo, records ->
            records.each { record ->
                data?.findAll { it.CondRecNo == condRecNo }.each { scaleRecord ->
                    def newRecord = [:]
                    newRecord.putAll(record)
                    newRecord.ScaleQuantity = scaleRecord.ScaleQuantity
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
return api.global.ZDGS
