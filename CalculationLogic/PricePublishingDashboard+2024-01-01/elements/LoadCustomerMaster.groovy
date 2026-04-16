def salesPersons = api.local.quotes?.collect { it.SalesPerson }?:[]
def shipTos = api.local.quotes?.collect{ it.ShipTo }?:[]
def soldTos = api.local.quotes?.collect{ it.SoldTo }?:[]

def filters = [
        Filter.in("customerId", salesPersons + shipTos + soldTos)
]

def records = api.stream("C", null, *filters)
        ?.withCloseable { it.collect()}

api.global.customers = records

return records
