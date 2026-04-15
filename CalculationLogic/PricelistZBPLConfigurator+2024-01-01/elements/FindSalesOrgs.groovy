if (api.global.SalesOrgsCache) return api.global.SalesOrgsCache

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("SalesOrg")

def result = qapi.source(t1, [t1.key1().as("name"), t1.SODescription.as("attribute1")], t1."Show on List Price Maintenance".equal(true))
        .stream { it.collect() }

api.global.SalesOrgsCache = result

return api.global.SalesOrgsCache