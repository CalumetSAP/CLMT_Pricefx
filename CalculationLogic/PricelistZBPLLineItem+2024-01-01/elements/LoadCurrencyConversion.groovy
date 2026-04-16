if(api.global.isFirstRow){
    def today = new Date()

    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ccy")
    def query = ctx.newQuery(dm)
            .select("CcyFrom")
            .select("CcyTo")
            .select("CcyValidFrom")
            .select("CcyExchangeRate")
            .where(
                    Filter.equal("CcyTo", "USD"),
                    Filter.lessOrEqual("CcyValidFrom", today)
            )
    query.orderBy("CcyFrom ASC", "CcyValidFrom DESC")

    def result = ctx.executeQuery(query)
    def data = result?.getData()

    def conversionMap = [:]
    def groupedData = data.groupBy { it.CcyFrom }

    groupedData.each { currency, records ->
        def currencyData = records.max { it.CcyValidFrom }
        if (currencyData) {
            conversionMap[currency] = currencyData.CcyExchangeRate.toBigDecimal() < BigDecimal.ZERO
                    ? "-1".toBigDecimal() / currencyData.CcyExchangeRate.toBigDecimal()
                    : currencyData.CcyExchangeRate.toBigDecimal()
        }
    }

    api.global.conversionMap = conversionMap
}

return api.global.conversionMap