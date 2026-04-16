def products = api.local.quotes?.collect { it.Material }?:[]

def filters = [
        Filter.in("sku", products)
]

def records = api.stream("P", null, *filters)
        ?.withCloseable { it.collect()}

api.global.products = records

return records
