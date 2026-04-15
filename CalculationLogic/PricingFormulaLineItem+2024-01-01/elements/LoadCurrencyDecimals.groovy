if (!out.LoadQuotes?.NumberofDecimals && !api.global.currencyDecimals) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("CurrencyDecimals")

    api.global.currencyDecimals = qapi.source(t1, [t1.key1(), t1.NumberOfDecimals]).stream { it.collect() }.groupBy { it.key1 }
}

return api.global.currencyDecimals?.get(out.LoadQuotes.Currency)?.find()?.NumberOfDecimals