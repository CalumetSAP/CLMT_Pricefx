if (api.global.PricelistCache) return api.global.PricelistCache

def fields = ["name", "attribute1"]

def result = api.findLookupTableValues("Pricelist", fields, "name")

api.global.PricelistCache = result

return api.global.PricelistCache