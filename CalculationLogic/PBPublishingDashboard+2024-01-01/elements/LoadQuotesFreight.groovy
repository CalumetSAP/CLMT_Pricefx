//def dmCtx = api.getDatamartContext()
//def configurator = out.Filters
//
//def getProductFromFilter(productGroupInput, List<String> ph1, List<String> ph2, List<String> ph3, List<String> ph4, List<String> brand) {
//
//    def filter = null
//
//    if (productGroupInput?.productFieldValue) {
//        filter = Filter.equal(productGroupInput?.productFieldName, productGroupInput?.productFieldValue)
//    } else if(productGroupInput?.productFilterCriteria){
//        filter = api.filterFromMap(productGroupInput?.productFilterCriteria)
//    }
//
//    def otherFilters = []
//    if(ph1) otherFilters.add(Filter.in("attribute14", ph1))
//    if(ph2) otherFilters.add(Filter.in("attribute16", ph2))
//    if(ph3) otherFilters.add(Filter.in("attribute18", ph3))
//    if(ph4) otherFilters.add(Filter.in("attribute20", ph4))
//    if(brand) otherFilters.add(Filter.in("attribute2", brand))
//
//    def fields = ["sku", "label"]
//
//    def products = api.stream("P", "sku", fields, true, *[filter, *otherFilters])?.withCloseable { it.collect{ it.sku } }
//
//    return products
//}
//
//String pricingDate = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY)
//List<String> salesOrg = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SALES_ORG_INPUT_KEY)
//List<String> soldTo = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SOLD_TO_INPUT_KEY)
//List<String> shipTo = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_INPUT_KEY)
//String division = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.DIVISION_INPUT_KEY)
////String masterParent = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.MASTER_PARENT_INPUT_KEY)
//List<String> ph1 = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_1_INPUT_KEY)
//List<String> ph2 = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_2_INPUT_KEY)
//List<String> ph3 = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_3_INPUT_KEY)
//List<String> ph4 = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_4_INPUT_KEY)
//
//List<String> brand = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.BRAND_INPUT_KEY)
//def products = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCTS_INPUT_KEY)
//List<String> contract = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.CONTRACT_INPUT_KEY)//["TestContract1", "AOTest"]//["40022330", "40022318", "40027164"]
//List<String> contractLine = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.CONTRACT_LINE_INPUT_KEY)//["jl2", "2550"]//["10", "30"]
//
//def materials = null
//
//if(products || ph1 || ph2 || ph3 || ph4 || brand) {
//    materials = getProductFromFilter(products, ph1, ph2, ph3, ph4, brand)
//}
//
//def ds = dmCtx.getDataSource("Quotes")
//def query = dmCtx.newQuery(ds, false)
//query.select("SAPContractNumber", "SAPContractNumber")
//query.select("SAPLineID", "SAPLineID")
//query.select("FreightValidFrom", "FreightValidFrom")
//query.select("FreightValidto", "FreightValidTo")
//query.select("FreightAmount", "FreightAmount")
//query.select("FreightUOM", "FreightUOM")
//
////query.where(Filter.isNotNull("Price"))
////query.where(Filter.isNotEmpty("Price"))
//
//if (pricingDate) {
//    query.where(
//            Filter.and(
//                    Filter.lessOrEqual("FreightValidFrom", pricingDate),
//                    Filter.greaterOrEqual("FreightValidTo", pricingDate)
//            )
//    )
//}
//
//if(salesOrg) query.where(Filter.in("SalesOrg", salesOrg))
//if(soldTo) query.where(Filter.in("SoldTo", soldTo))
//if(shipTo) query.where(Filter.in("ShipTo", shipTo))
//if(division) query.where(Filter.equal("Division", division))
//if(materials) query.where(Filter.in("Material", materials))
//if(contract) query.where(Filter.in("SAPContractNumber", contract))
//if(contractLine) query.where(Filter.in("SAPLineID", contractLine))
//query.where(Filter.notEqual("RejectionFlag", true))
//
////query.where(Filter.or(Filter.isNull("RejectionReason"), Filter.equal("RejectionReason", "")))
//query.orderBy("QuoteLastUpdate DESC")
//
//def result = [:]
//
//if(contract || contractLine || materials || division || shipTo || soldTo || salesOrg) {
//    def key
//    dmCtx.executeQuery(query)?.getData()?.each{ it ->
//        key = it.SAPContractNumber + "|" + it.SAPLineID
//        result.putIfAbsent(key, it)
//    }
//}
//
//api.local.freightQuotes = result