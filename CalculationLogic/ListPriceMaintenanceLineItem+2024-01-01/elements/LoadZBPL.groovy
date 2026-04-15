String material = api.local.material
String salesOrg = api.local.salesOrg
String pricelist = api.local.pricelist

if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.pricelists) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZBPL")

    List customFilters = [
            Filter.in("Material", api.global.currentBatch),
            Filter.in("Pricelist", api.global.pricelists),
            Filter.in("SalesOrganization", api.global.salesOrgs)
    ]

    if (api.global.noEffectiveDatesOverridden) {
        customFilters.add(Filter.lessOrEqual("ValidFrom", api.local.newEffectiveDate))
        customFilters.add(Filter.greaterOrEqual("ValidTo", api.local.newEffectiveDate))
    } else {
        customFilters.add(Filter.lessOrEqual("ValidFrom", api.global.maxNewEffectiveDate))
        customFilters.add(Filter.greaterOrEqual("ValidTo", api.global.minNewEffectiveDate))
    }

    def query = ctx.newQuery(dm, false)
            .select("Material", "Material")
            .select("ValidFrom", "ValidFrom")
            .select("ValidTo", "ValidTo")
            .select("SalesOrganization", "SalesOrganization")
            .select("DistributionChannel", "DistributionChannel")
            .select("ConditionRecordNo", "ConditionRecordNo")
            .select("Pricelist", "Pricelist")
            .select("Amount", "Amount")
            .select("Per", "Per")
            .select("UnitOfMeasure", "UnitOfMeasure")
            .select("ConditionCurrency", "ConditionCurrency")
            .select("lastUpdateDate", "lastUpdateDate")
            .where(*customFilters)
            .orderBy("lastUpdateDate DESC")

    api.global.ZBPL = ctx.executeQuery(query)?.getData()?.groupBy {[it.Material, it.SalesOrganization, it.Pricelist] } ?: [:]
}

if (pricelist) {
    if (api.global.noEffectiveDatesOverridden) {
        return api.global.ZBPL?.get([material, salesOrg, pricelist])?.find() ?: [:]
    } else {
        return api.global.ZBPL?.get([material, salesOrg, pricelist])?.find {
            it.ValidFrom <= api.local.newEffectiveDate && it.ValidTo >= api.local.newEffectiveDate
        } ?: [:]
    }
} else {
    return [:]
}