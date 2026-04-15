if (api.isInputGenerationExecution() || !api.local.soldTo) return [:]

def qapi = api.queryApi()
def t1 = qapi.tables().customers()
def fields = [t1.customerId(), t1.name(), t1.Street, t1.City, t1.Region, t1.PostalCode, t1.Country]

return qapi.source(t1, fields, t1.customerId().in(api.local.soldTo as List)).stream {
    it.collectEntries {
        [(it.customerId): [
                Name   : it.name,
                Address: it.Street,
                City   : it.City,
                State  : it.Region,
                ZIP    : it.PostalCode,
                Country: it.Country,
                Contact: it.name
        ]]
    }
}