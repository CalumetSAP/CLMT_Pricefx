//if (!quoteProcessor.isPostPhase()) return
//if (!api.local.pricelists) return
//
//final tablesConstants = libs.QuoteConstantsLibrary.Tables
//final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
//
//def materials = api.local.skusWithPL
//
//def ctx = api.getDatamartContext()
//def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_LIST_PRICE)
//
//def effectiveDate = quoteProcessor.getQuoteView().targetDate
//def expiryDate = quoteProcessor.getQuoteView().expiryDate
//
//def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
//        headerConstants.INPUTS_NAME
//)?.value ?: [:]
//
//def selectedSalesOrg = customerConfigurator?.get(headerConstants.SALES_ORG_ID)
//
//def customFilter = Filter.and(
//        Filter.lessOrEqual("Valid_From", effectiveDate),
//        Filter.greaterOrEqual("Valid_To", expiryDate),
//        Filter.in("Material", materials),
//        Filter.equal("Sales_Org", selectedSalesOrg),
//)
//
//def query = ctx.newQuery(dm, false)
//        .select("Material", "Material")
//        .select("Amount", "Amount")
//        .select("UOM", "UOM")
//        .select("Valid_From", "Valid_From")
//        .select("Valid_To", "Valid_To")
//        .where(customFilter)
//        .orderBy("Valid_From DESC", "Valid_To DESC")
//
//def result = ctx.executeQuery(query)
//def data = result?.getData()
//def pricingMap = [:]
//
//data?.each {
//    pricingMap.putIfAbsent(it.Material, it)
//}
//
//return pricingMap
