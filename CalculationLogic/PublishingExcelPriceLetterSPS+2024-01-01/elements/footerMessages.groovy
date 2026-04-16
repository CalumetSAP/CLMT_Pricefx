def productFilters = [
        Filter.in("sku", api.local.materials as List<String>)
]

def ph1 = api.stream("P", "sku", ["attribute14"], true, *productFilters)?.withCloseable { it.collect{ it.attribute14 } }

def filters = [
        Filter.in("key1", api.local.salesOrg as List<String>),
        Filter.in("key2", ph1 as List<String>)
]

def footerMessages = api.findLookupTableValues("FooterEmails", ["attribute1"], "attribute1", *filters)?.collect{it.attribute1}?.unique()?:[]
def bulkRailMessage = ["Orders in railcar are subject to demurrage charges of \$100 USD/day for railcars that are not unloaded and released within 15 days of arrival"]

return api.local.hasBulkRailItem ? bulkRailMessage + footerMessages : footerMessages
//return footerMessages