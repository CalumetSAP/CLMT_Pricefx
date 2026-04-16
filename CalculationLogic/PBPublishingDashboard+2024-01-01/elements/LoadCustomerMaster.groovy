def salesPersons = api.local.allRows?.collect { it.SalesPerson }?.unique() ?: []
def shipTos = api.local.allRows?.collect{ it.ShipTo }?.unique() ?: []
def soldTos = api.local.allRows?.collect{ it.SoldTo }?.unique() ?: []

def filters = [
        Filter.in("customerId", salesPersons + shipTos + soldTos)
]

def records = api.stream("C", null, *filters)
        ?.withCloseable { it.collect()}

api.global.customers = records

return records
