def productFilters = [
        Filter.in("sku", api.local.materials as List<String>)
]

def ph1 = api.stream("P", "sku", ["attribute14"], true, *productFilters)?.withCloseable { it.collect{ it.attribute14 } }

def filters = [
        Filter.in("key1", api.local.salesOrg as List<String>),
        Filter.in("key2", ph1 as List<String>)
]

return api.findLookupTableValues("FooterEmails", ["attribute1"], "attribute1", *filters)?.collect{["footer": it.attribute1]}?.unique()?:[]