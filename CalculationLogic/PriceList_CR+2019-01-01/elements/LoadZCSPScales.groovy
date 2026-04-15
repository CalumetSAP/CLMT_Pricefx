//api.local.zcspScales = [:]
//
//if (api.local.isMassEdit) {
//    def conditionRecords = api.local.plItems["Condition Record No"].unique() //This field was removed from the pricelists columns
//    conditionRecords.remove(null)
//
//    def ctx = api.getDatamartContext()
//    def dm = ctx.getDataSource("ZCSPScales")
//
//    def query = ctx.newQuery(dm, false)
//            .select("ConditionRecordNo", "ConditionRecordNo")
//            .select("ScaleQuantity", "ScaleQuantity")
//            .select("ConditionRate", "ConditionRate")
//            .where(Filter.in("ConditionRecordNo", conditionRecords))
//            .orderBy("ScaleQuantity")
//
//    def scalesAux
//    api.local.zcspScales = ctx.executeQuery(query)?.getData()?.groupBy {
//        it.ConditionRecordNo
//    }?.collectEntries { conditionRecordNo, row ->
//        scalesAux = row.collect { it.ScaleQuantity+"="+it.ConditionRate }
//        [conditionRecordNo, scalesAux.join("|")]
//    } ?: [:]
//}
//
//return null