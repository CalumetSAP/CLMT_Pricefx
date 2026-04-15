if (api.global.SalesOrgsCache) return api.global.SalesOrgsCache

def fields = ["name", "attribute1"]

def result = api.findLookupTableValues("SalesOrg", fields, "name", Filter.equal("attribute4", true))

api.global.SalesOrgsCache = result

return api.global.SalesOrgsCache