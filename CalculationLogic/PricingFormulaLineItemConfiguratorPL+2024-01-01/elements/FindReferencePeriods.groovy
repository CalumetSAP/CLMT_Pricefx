if (api.global.referencePeriods) return api.global.referencePeriods

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows(libs.QuoteConstantsLibrary.Tables.DROPDOWN_OPTIONS)
def filter = qapi.exprs().and(
        t1.key1().equal("Quote"),
        t1.key2().equal("ReferencePeriod"),
)

api.global.referencePeriods = qapi.source(t1, [t1.key3(), t1."Attribute 1"], filter).stream { it.collectEntries {
    [(it.key3): it."Attribute 1"]
} } ?: [:]

return api.global.referencePeriods