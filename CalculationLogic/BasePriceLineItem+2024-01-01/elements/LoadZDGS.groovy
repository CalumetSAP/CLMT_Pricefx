// when the new batch starts, do pre-load ZDGS (DS) (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def materials = api.global.currentBatch

    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZDGS")

    def today = new Date()

    def customFilter = Filter.and(
            Filter.lessOrEqual("ValidFrom", today),
            Filter.greaterOrEqual("ValidTo", today),
            Filter.in("Material", materials)
    )

    def query = ctx.newQuery(dm, false)
            .select("Material", "Material")
            .select("CondRecNo", "CondRecNo")
            .select("Amount", "Amount")
            .select("UoM", "UoM")
            .select("PL", "PL")
            .select("ValidFrom", "ValidFrom")
            .select("ValidTo", "ValidTo")
            .where(customFilter)
            .orderBy("ValidFrom DESC", "ValidTo DESC")

    def result = ctx.executeQuery(query)
    def data = result?.getData()
    def ZDGS = [:]

    data?.each {
        def key = it.Material + '|' + it.PL
        def condRecNo = it.CondRecNo
        if (!ZDGS.containsKey(key)) {
            ZDGS[key] = [:]
        }
        if (!ZDGS[key].containsKey(condRecNo)) {
            ZDGS[key][condRecNo] = []
        }
        ZDGS[key][condRecNo] << it
    }

    api.global.ZDGS = ZDGS
}
