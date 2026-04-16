//if (libs.SharedLib.BatchUtils.isNewBatch()) {
//    Date effectiveDate = api.global.calculationDate
//    def filters = [
//            Filter.lessOrEqual("ValidFrom", effectiveDate),
//            Filter.greaterOrEqual("ValidTo", effectiveDate),
//            Filter.in("Material", api.global.currentBatch),
//    ]
//
//    def ctx = api.getDatamartContext()
//    def ds = ctx.getDataSource("ZPFX")
//
//    def query = ctx.newQuery(ds)
//            .select("ConditionRecordNo", "ConditionRecordNo")
//            .select("SalesOrganization", "SalesOrganization")
//            .select("ContractNumber", "ContractNumber")
//            .select("SalesDocumentItem", "SalesDocumentItem")
//            .select("Division", "Division")
//            .select("SoldToParty", "SoldToParty")
//            .select("Material", "Material")
//            .select("UnitOfMeasure", "UnitOfMeasure")
//            .select("Amount", "Amount")
//            .select("PricingUnit", "PricingUnit")
//            .select("ConditionCurrency", "ConditionCurrency")
//            .select("lastUpdateDate", "lastUpdateDate")
//            .setUseCache(false)
//            .where(*filters)
//            .orderBy("lastUpdateDate DESC")
//
//    def zpfx = ctx.executeQuery(query)?.getData()
//
//    List<Object> quotes = api.global.quotes?.collect()
//    //Any Ship To
//    List<String> divisions = quotes?.Division?.unique() ?: []
//    if (divisions?.contains("20")) {
//        api.global.shipToAndDivision20Map = zpfx.groupBy { [it.SalesOrganization, it.ContractNumber, it.SalesDocumentItem, it.Material] }
//    }
//    if (divisions?.contains("30")) {
//        api.global.shipToAndDivision30Map = zpfx.groupBy { [it.SalesOrganization, it.ContractNumber, it.Material] }
//    }
//}
//
//def quote = out.LoadQuotes
//String division = quote?.Division
//String salesOrg = quote?.SalesOrg
//String material = api.local.material
//
//String contractNumber = quote?.SAPContractNumber
//if (!contractNumber) {
//    return null
//}
//if (division == "20") {
//    String contractItem = quote?.SAPLineID
//    return api.global.shipToAndDivision20Map?.get([salesOrg, contractNumber, contractItem, material])?.first()
//}
//if (division == "30") {
//    return api.global.shipToAndDivision30Map?.get([salesOrg, contractNumber, material])?.first()
//}
