if(api.global.isFirstRow){
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("Pricelist")

    api.global.cptPricelists = qapi.source(t1, [t1.key1(), t1.Description])
            .sortBy { cols -> [qapi.orders().ascNullsLast(cols.key1)] }
            .stream { it.collectEntries { [(it.key1): it.Description] } } ?: [:]

}

return api.global.cptPricelists?.get(api.local.pricelist)