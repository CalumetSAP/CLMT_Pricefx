if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.pricelists) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZBPL")

    def customFilter = Filter.and(
//            Filter.lessOrEqual("ValidFrom", api.global.effectiveDate),
//            Filter.greaterOrEqual("ValidTo", api.global.effectiveDate),
            Filter.in("Material", api.global.currentBatch),
            Filter.in("Pricelist", (api.global.pricelists + api.global.pricelistsNewItems).unique()),
            Filter.in("SalesOrganization", api.global.salesOrgs)
    )

    def query = ctx.newQuery(dm, false)
            .select("Material", "Material")
            .select("SalesOrganization", "SalesOrganization")
            .select("Pricelist", "Pricelist")
            .select("ValidFrom", "ValidFrom")
            .select("ValidTo", "ValidTo")
            .select("lastUpdateDate", "lastUpdateDate")
            .where(customFilter)
            .orderBy("lastUpdateDate DESC")

    api.global.ZBPL = ctx.executeQuery(query)?.getData()?.
            groupBy {[it.Material, it.SalesOrganization, it.Pricelist] } ?: [:]
}

return api.global.ZBPL