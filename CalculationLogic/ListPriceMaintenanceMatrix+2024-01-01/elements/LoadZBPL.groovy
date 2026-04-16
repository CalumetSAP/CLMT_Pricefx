if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.pricelists) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZBPL")

    def customFilter = Filter.and(
            Filter.lessOrEqual("ValidFrom", api.global.effectiveDate),
            Filter.greaterOrEqual("ValidTo", api.global.effectiveDate),
            Filter.in("Material", api.global.currentBatch),
            Filter.in("Pricelist", api.global.pricelists),
            Filter.in("SalesOrganization", api.global.salesOrgs)
    )

    def query = ctx.newQuery(dm, false)
            .select("Material", "Material")
            .select("SalesOrganization", "SalesOrganization")
            .select("ConditionRecordNo", "ConditionRecordNo")
            .select("Pricelist", "Pricelist")
            .select("lastUpdateDate", "lastUpdateDate")
            .where(customFilter)
            .orderBy("lastUpdateDate DESC")

    api.global.ZBPL = ctx.executeQuery(query)?.getData()?.
            groupBy {[it.Material, it.SalesOrganization, it.Pricelist] }?.
            collectEntries {[(it.key): it.value.find()]} ?: [:]
}

return api.global.ZBPL