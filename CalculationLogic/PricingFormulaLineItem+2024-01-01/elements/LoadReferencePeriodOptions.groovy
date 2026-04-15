if (api.global.isFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows(libs.QuoteConstantsLibrary.Tables.DROPDOWN_OPTIONS)
    def filter = qapi.exprs().and(
            t1.key1().equal("Quote"),
            t1.key2().equal("ReferencePeriod"),
    )

    api.global.referencePeriodOptions = qapi.source(t1, [t1.key3(), t1."Attribute 1"], filter)
            .sortBy { cols -> [qapi.orders().ascNullsLast(cols.key3)] }
            .stream { it.collectEntries {[(it."Attribute 1"): it.key3] } } ?: [:]

}

return api.global.referencePeriodOptions ?: [:]