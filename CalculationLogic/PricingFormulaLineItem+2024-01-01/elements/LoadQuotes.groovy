def material = api.local.material
def secondaryKey = api.local.secondaryKey

if (api.global.isFirstRow) {
    Set<String> fields = ["QuoteID", "LineID", "UpdatedbyID", "SalesOrg", "Material", "SoldTo","SoldtoName", "ShipTo", "ShiptoName",
                          "SAPContractNumber", "SAPLineID", "Division", "Price", "PricingUOM", "Per", "NumberofDecimals", "IndexNumberOne",
                          "IndexNumberTwo", "IndexNumberThree", "IndexNumberOnePercent", "IndexNumberTwoPercent",
                          "IndexNumberThreePercent", "Adder", "AdderUOM", "ReferencePeriod", "ReferencePeriodValue",
                          "RecalculationPeriod", "RecalculationDate", "Currency"]
    api.global.quotes = libs.PricelistLib.Query.getValidIndexQuotesRows(fields, api.global.calculationDate, api.global.recalculationPeriods, api.global.referencePeriods, api.global.indexNumbers)
    api.global.groupedQuotes = api.global.quotes?.groupBy {
        (it.Material?:"")+"-"+(it.SoldTo?:"")+"-"+(it.ShipTo?:"")+"-"+(it.SAPContractNumber?:"")+"-"+(it.SAPLineID?:"")
    } ?: [:]
}

return api.global.groupedQuotes[material+"-"+secondaryKey]?.find() ?: [:]