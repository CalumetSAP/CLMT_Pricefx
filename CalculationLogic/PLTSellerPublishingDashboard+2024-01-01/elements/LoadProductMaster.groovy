def products = api.global.materials

def filters = [
        Filter.in("sku", products)
]

def records = api.stream("P", null, *filters)
        ?.withCloseable { it.collect()}

api.global.products = records

return null
