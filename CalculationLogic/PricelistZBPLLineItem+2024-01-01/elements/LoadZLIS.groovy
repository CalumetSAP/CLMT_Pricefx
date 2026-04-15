String material = api.local.material
String salesOrg = api.local.salesOrg

// when the new batch starts, do pre-load ListPrice (DS) (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ListPrice")

    List customFilters = [
            Filter.equal("A_Table_Number", "A901"),
            Filter.in("Material", api.global.currentBatch),
            Filter.in("Sales_Org", api.global.salesOrgs)
    ]

    if (api.global.noEffectiveDatesOverridden) {
        customFilters.add(Filter.lessOrEqual("Valid_From", api.local.newEffectiveDate))
        customFilters.add(Filter.greaterOrEqual("Valid_To", api.local.newEffectiveDate))
    } else {
        customFilters.add(Filter.lessOrEqual("Valid_From", api.global.maxNewEffectiveDate))
        customFilters.add(Filter.greaterOrEqual("Valid_To", api.global.minNewEffectiveDate))
    }

    def query = ctx.newQuery(dm, false)
            .select("Sales_Org", "Sales_Org")
            .select("Currency", "Currency")
            .select("Material", "Material")
            .select("Valid_From", "Valid_From")
            .select("Valid_To", "Valid_To")
            .select("Amount", "Amount")
            .select("UOM", "UOM")
            .select("lastUpdateDate", "lastUpdateDate")
            .where(*customFilters)
            .orderBy("lastUpdateDate DESC")

    api.global.ZLIS = ctx.executeQuery(query)?.getData()?.groupBy {[it.Material, it.Sales_Org] } ?: [:]
}

if (api.global.noEffectiveDatesOverridden) {
    return api.global.ZLIS?.get([material, salesOrg])?.find() ?: [:]
} else {
    return api.global.ZLIS?.get([material, salesOrg])?.find {
        it.Valid_From <= api.local.newEffectiveDate && it.Valid_To >= api.local.newEffectiveDate
    } ?: [:]
}