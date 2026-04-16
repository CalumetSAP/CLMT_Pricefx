if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ListPrice")

    def customFilter = Filter.and(
            Filter.lessOrEqual("Valid_From", api.global.effectiveDate),
            Filter.greaterOrEqual("Valid_To", api.global.effectiveDate),
            Filter.equal("A_Table_Number", "A901"),
            Filter.in("Material", api.global.currentBatch),
            Filter.in("Sales_Org", api.global.salesOrgs)
    )

    def query = ctx.newQuery(dm, false)
            .select("Material", "Material")
            .select("Sales_Org", "Sales_Org")
            .where(customFilter)
            .orderBy("lastUpdateDate DESC")

    api.global.ZLIS = ctx.executeQuery(query)?.getData()?.groupBy {[it.Material, it.Sales_Org] } ?: [:]
}

return api.global.ZLIS