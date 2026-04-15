def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows("VariantsPBPricePublishingDashboard")

def variants = qapi.source(t1)
        .sortBy {cols -> [qapi.orders().ascNullsLast(cols.key1)]}
        .stream { it.collect { it } }

return variants?:[]