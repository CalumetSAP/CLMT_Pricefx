if (!api.global.PricelistCache) {
    def fields = ["name", "attribute1"]

    def result = api.findLookupTableValues("Pricelist", fields, "name")

    api.global.PricelistCache = result?.collect { it.name + " - " + it.attribute1 }
}

return api.global.PricelistCache