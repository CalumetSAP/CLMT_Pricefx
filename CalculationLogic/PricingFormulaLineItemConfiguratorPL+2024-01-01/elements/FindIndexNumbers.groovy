if (api.global.indexNumbers) return api.global.indexNumbers

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows(libs.QuoteConstantsLibrary.Tables.INDEX_VALUES)

api.global.indexNumbers = qapi.source(t1, [t1.key1(), t1.key2(), t1.DescrOfPriceQuote]).stream { it.collect {
    it.key1 + "-" + it.key2 + "-" + it.DescrOfPriceQuote
} } ?: []

return api.global.indexNumbers