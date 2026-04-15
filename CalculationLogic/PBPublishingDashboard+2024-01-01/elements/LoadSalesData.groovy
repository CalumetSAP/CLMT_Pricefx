List<String> salesOrg = api.local.allRows?.collect { it.SalesOrg }?.unique()
List<String> division = api.local.allRows?.collect { it.Division }?.unique()
List<String> soldTo = api.local.allRows?.collect { it.SoldTo }?.unique()

def filters = [
        Filter.equal("name", "CMSalesDataKNVV"),
        Filter.in("attribute1", salesOrg),
        Filter.in("attribute3", division),
        Filter.in("customerId", soldTo)
]

def salesData = api.stream("CX20", "customerId", ["customerId", "attribute6"], true, *filters)?.withCloseable { it.collect() }

return salesData