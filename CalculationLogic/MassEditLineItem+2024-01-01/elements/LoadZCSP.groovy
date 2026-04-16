if (api.global.isFirstRow) {
    //TODO if "effective date" manual override is implemented, check what to do

    List fromDates = [api.global.effectiveDate, api.global.announcementDate, api.global.priceLetterDate]
    api.global.minPossibleFromDate = fromDates.min()
    def maxFromDate = Calendar.getInstance()
    maxFromDate.setTime(fromDates.max())
    maxFromDate.add(Calendar.YEAR, 1) //TODO could the max be smaller?
    api.global.maxPossibleFromDate = maxFromDate.getTime()
}

if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def filters = [
            Filter.lessOrEqual("ValidFrom", api.global.maxPossibleFromDate),
            Filter.greaterOrEqual("ValidTo", api.global.minPossibleFromDate),
            Filter.in("Material", api.global.currentBatch),
    ]
    if (api.global.salesOrgs) {
        filters.add(Filter.in("SalesOrganization", api.global.salesOrgs))
    }

    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("ZCSP")

    def query = ctx.newQuery(ds)
            .select("SalesOrganization", "SalesOrganization")
            .select("ContractNumber", "ContractNumber")
            .select("SalesDocumentItem", "SalesDocumentItem")
            .select("Division", "Division")
            .select("SoldToParty", "SoldToParty")
            .select("Material", "Material")
            .select("ValidFrom", "ValidFrom")
            .select("ValidTo", "ValidTo")
            .select("UnitOfMeasure", "UnitOfMeasure")
            .select("Amount", "Amount")
            .select("PricingUnit", "PricingUnit")
            .select("ConditionCurrency", "ConditionCurrency")
            .select("lastUpdateDate", "lastUpdateDate")
            .setUseCache(false)
            .where(*filters)
            .orderBy("lastUpdateDate DESC")

    def zcsp = ctx.executeQuery(query)?.getData()

    List<Object> quotes = api.global.quotes?.collect()
    List<Boolean> soldToLevelIndicators = quotes?.SoldToLevelIndicator?.unique() ?: []
    //Any Ship To
    if (soldToLevelIndicators?.any { !it }) {
        List<String> divisions = quotes?.Division?.unique() ?: []
        if (divisions?.contains("20")) {
            api.global.shipToAndDivision20Map = zcsp.groupBy { [it.SalesOrganization, it.ContractNumber, it.SalesDocumentItem, it.Material] }
        }
        if (divisions?.contains("30")) {
            api.global.shipToAndDivision30Map = zcsp.groupBy { [it.SalesOrganization, it.ContractNumber, it.Material] }
        }
    }
    //Any Sold To
    if (soldToLevelIndicators?.any { it }) {
        api.global.noShipToMap = zcsp.groupBy { [it.SalesOrganization, it.Division, it.SoldToParty, it.Material] }
    }
}

def quote = out.LoadQuotes
String division = quote?.Division
String salesOrg = quote?.SalesOrg
String material = api.local.material
Date calculatedEffectiveDate = out.CalculateEffectiveDate

if (quote?.SoldToLevelIndicator) {
    String soldTo = quote?.SoldTo
    return api.global.noShipToMap?.get([salesOrg, division, soldTo, material])
            ?.find { it.ValidFrom <= calculatedEffectiveDate && it.ValidTo >= calculatedEffectiveDate}
} else {
    String contractNumber = quote?.SAPContractNumber
    if (!contractNumber) {
        return null
    }
    if (division == "20") {
        String contractItem = quote?.SAPLineID
        return api.global.shipToAndDivision20Map?.get([salesOrg, contractNumber, contractItem, material])
                ?.find { it.ValidFrom <= calculatedEffectiveDate && it.ValidTo >= calculatedEffectiveDate}
    }
    if (division == "30") {
        return api.global.shipToAndDivision30Map?.get([salesOrg, contractNumber, material])
                ?.find { it.ValidFrom <= calculatedEffectiveDate && it.ValidTo >= calculatedEffectiveDate}
    }
}

return null