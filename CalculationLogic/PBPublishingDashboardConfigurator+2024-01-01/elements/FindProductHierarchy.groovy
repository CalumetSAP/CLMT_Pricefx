def filters = [
        Filter.equal("name", "ProductHierarchy")
]

def productHierarchy = api.stream("PX10", "sku", ["sku", "attribute1"], true, *filters)?.withCloseable { it.collect() }

return productHierarchy