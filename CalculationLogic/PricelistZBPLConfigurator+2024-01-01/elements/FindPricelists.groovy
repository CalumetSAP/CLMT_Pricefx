if (!api.global.PricelistCache) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("Pricelist")

    api.global.PricelistCache = qapi.source(t1, [t1.key1(), t1.Description])
            .sortBy { cols -> [qapi.orders().ascNullsLast(cols.key1)] }
            .stream { it.collect { it.key1 + " - " + it.Description} }
}

return api.global.PricelistCache