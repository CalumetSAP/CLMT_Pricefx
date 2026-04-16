if (!out.LoadQuotes?.NumberofDecimals && !api.global.currencyDecimals) {
    api.global.currencyDecimals = api.findLookupTableValues("CurrencyDecimals").groupBy { it.name }
}

return api.global.currencyDecimals?.get(out.LoadQuotes.Currency)?.find()?.attribute1