if(api.global.isFirstRow){
    def today = new Date()

    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("CurrencyConversion")
    def query = ctx.newQuery(dm)
            .select("FromCurrency")
            .select("ToCurrency")
            .select("Validfrom")
            .select("ExchangeRate")
            .where(
                    Filter.equal("ToCurrency", "USD"),
                    Filter.lessOrEqual("Validfrom", today)
            )
    query.orderBy("FromCurrency ASC", "Validfrom DESC")

    def result = ctx.executeQuery(query)
    def data = result?.getData()

    def conversionMap = [:]
    def groupedData = data.groupBy { it.FromCurrency }

    groupedData.each { currency, records ->
        def currencyData = records.max { it.Validfrom }
        if (currencyData) {
            conversionMap[currency] = currencyData.ExchangeRate.toBigDecimal() < BigDecimal.ZERO
                    ? "-1".toBigDecimal() / currencyData.ExchangeRate.toBigDecimal()
                    : currencyData.ExchangeRate.toBigDecimal()
        }
    }

    return conversionMap
}
