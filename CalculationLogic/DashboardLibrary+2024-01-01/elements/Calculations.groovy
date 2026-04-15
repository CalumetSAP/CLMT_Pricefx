List findPossibleMaterialsForVariants(variant) {
    def savedProducts = variant.Products
    def hasOtherData = variant.PH1 || variant.PH2 || variant.PH3 || variant.PH4 || variant.Brand
    if (!savedProducts && !hasOtherData) return null

    def filter = null

    if (savedProducts) {
        if (savedProducts?.productFieldValue) {
            filter = Filter.equal(savedProducts?.productFieldName, savedProducts?.productFieldValue)
        } else if(savedProducts?.productFilterCriteria){
            filter = api.filterFromMap(savedProducts?.productFilterCriteria)
        }
    }

    def otherFilters = []
    if (variant.PH1) otherFilters.add(Filter.in("attribute14", variant.PH1))
    if (variant.PH2) otherFilters.add(Filter.in("attribute16", variant.PH2))
    if (variant.PH3) otherFilters.add(Filter.in("attribute18", variant.PH3))
    if (variant.PH4) otherFilters.add(Filter.in("attribute20", variant.PH4))
    if (variant.Brand) otherFilters.add(Filter.in("attribute2", variant.Brand))

    def fields = ["sku", "label"]
    def finalFilter = []
    if (filter) finalFilter.add(filter)
    finalFilter.addAll(otherFilters)

    def products = api.stream("P", "sku", fields, true, *finalFilter)?.withCloseable { it.collect{ it.sku } }

    return products
}