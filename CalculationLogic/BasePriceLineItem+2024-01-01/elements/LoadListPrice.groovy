def material = api.local.material

// when the new batch starts, do pre-load ListPrice (DS) (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def materials = api.global.currentBatch

    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ListPrice")

    def today = new Date()

    def customFilter = Filter.and(
            Filter.lessOrEqual("Valid_From", today),
            Filter.greaterOrEqual("Valid_To", today),
            Filter.in("Material", materials)
    )

    def query = ctx.newQuery(dm, false)
            .select("Material", "Material")
            .select("Amount", "Amount")
            .select("UOM", "UOM")
            .select("Unit", "Unit")
            .select("Per", "Per")
            .select("Currency", "Currency")
            .select("Valid_From", "Valid_From")
            .select("Valid_To", "Valid_To")
            .where(customFilter)
            .orderBy("Valid_From DESC", "Valid_To DESC")

    def result = ctx.executeQuery(query)
    def data = result?.getData()
    def listPriceMap = [:]

    data?.each {
        listPriceMap.putIfAbsent(it.Material, it)
    }

    api.global.ListPriceMap = listPriceMap
}
