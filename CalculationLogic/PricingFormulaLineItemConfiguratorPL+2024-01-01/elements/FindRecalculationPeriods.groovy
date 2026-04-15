if (api.global.recalculationPeriods) return api.global.recalculationPeriods

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows(libs.QuoteConstantsLibrary.Tables.DROPDOWN_OPTIONS)
def filter = qapi.exprs().and(
        t1.key1().equal("Quote"),
        t1.key2().equal("RecalculationPeriod"),
)

api.global.recalculationPeriods = qapi.source(t1, [t1."Attribute 1"], filter).stream { it.collect { it."Attribute 1"} } ?: []

return api.global.recalculationPeriods