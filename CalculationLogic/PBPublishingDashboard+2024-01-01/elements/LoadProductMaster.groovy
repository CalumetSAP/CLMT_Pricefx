def products = api.local.allRows?.collect { it.Material }?.unique() ?: []

def filters = [
        Filter.in("sku", products)
]

def records = api.stream("P", null, *filters)
        ?.withCloseable { it.collect()}

api.global.products = records

return records
