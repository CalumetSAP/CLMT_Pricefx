List<String> salesOrg = api.local.quotes?.collect{it.SalesOrg}?.unique()
String division = api.local.quotes?.collect{it.Division}?.find{it}
List<String> soldTo = api.local.quotes?.collect{it.SoldTo}?.unique()

def filters = [
        Filter.equal("name", "CMSalesDataKNVV"),
        Filter.in("attribute1", salesOrg),
        Filter.equal("attribute3", division),
        Filter.in("customerId", soldTo)
]

def salesData = api.stream("CX20", "customerId", ["customerId", "attribute6"], true, *filters)?.withCloseable { it.collect() }

return salesData