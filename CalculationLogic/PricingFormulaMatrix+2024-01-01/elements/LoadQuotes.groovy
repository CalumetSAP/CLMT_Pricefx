if (api.global.isFirstRow) {
    Set<String> fields = ["Material", "SoldTo", "ShipTo", "SAPContractNumber", "SAPLineID"]
    api.global.quotes = libs.PricelistLib.Query.getValidIndexQuotesRows(fields, api.global.calculationDate, api.global.recalculationPeriods, api.global.referencePeriods, api.global.indexNumbers)?.groupBy { it.Material }
}

return null