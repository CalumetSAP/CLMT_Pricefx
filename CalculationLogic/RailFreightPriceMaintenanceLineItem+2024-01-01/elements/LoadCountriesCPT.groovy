if (api.global.isFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("FreightBusinessRulesCountries")

    api.global.countries = qapi.source(t1, [t1.key1()]).stream { it.collect {it.key1 } } ?: []
}

return null