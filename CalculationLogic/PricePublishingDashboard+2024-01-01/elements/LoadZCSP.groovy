//def products = api.global.products?.collect{it.sku}
//def salesOrgs = api.local.quotes?.collect { it.SalesOrg }
//
//def today = new Date()
//def filters = [
////        Filter.lessOrEqual("ValidFrom", today),
////        Filter.greaterOrEqual("ValidTo", today),
//        Filter.in("Material", products),
//]
//
//if (salesOrgs) {
//    filters.add(Filter.in("SalesOrganization", salesOrgs))
//}
//
//def ctx = api.getDatamartContext()
//def ds = ctx.getDataSource("ZCSP")
//
//def query = ctx.newQuery(ds)
//        .select("ConditionRecordNo", "ConditionRecordNo")
//        .select("SalesOrganization", "SalesOrganization")
//        .select("ContractNumber", "ContractNumber")
//        .select("SalesDocumentItem", "SalesDocumentItem")
//        .select("Division", "Division")
//        .select("SoldToParty", "SoldToParty")
//        .select("Material", "Material")
//        .select("ValidFrom", "ValidFrom")
//        .select("ValidTo", "ValidTo")
//        .select("UnitOfMeasure", "UnitOfMeasure")
//        .select("Amount", "Amount")
//        .select("PricingUnit", "PricingUnit")
//        .select("ConditionCurrency", "ConditionCurrency")
//        .select("lastUpdateDate", "lastUpdateDate")
//        .setUseCache(false)
//        .where(*filters)
//        .orderBy("lastUpdateDate DESC")
//
//def data = ctx.executeQuery(query)?.getData()
//api.global.zcsp = data
//api.global.zcsp20 = data?.groupBy { [it.SalesOrganization, it.ContractNumber, it.SalesDocumentItem, it.Material] }
//api.global.zcsp30 = data?.groupBy { [it.SalesOrganization, it.ContractNumber, it.Material] }
//api.global.zcspNoShipTo = data?.groupBy { [it.SalesOrganization, it.Division, it.SoldToParty, it.Material] }