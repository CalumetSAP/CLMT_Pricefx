if (!out.LoadQuotes?.NumberofDecimals && !api.global.currencyDecimals) {
    api.global.currencyDecimals = api.findLookupTableValues("CurrencyDecimals", ["name", "attribute1"], null).collectEntries { [(it.name): it.attribute1] }
}

return api.global.currencyDecimals?.get(out.MergeQuoteAndZCSP.Currency)?.toInteger()