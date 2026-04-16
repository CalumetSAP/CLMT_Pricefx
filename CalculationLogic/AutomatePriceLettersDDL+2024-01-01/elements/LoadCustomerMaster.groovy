if (api.isInputGenerationExecution()) return

def salesPersons = api.global.salesPersonList ?: []
def shipTos = api.global.shipToList ?: []
def soldTos = api.global.soldToList ?: []

def finalList = salesPersons + shipTos + soldTos
if (!finalList) return

def qapi = api.queryApi()
def t1 = qapi.tables().customers()
def fields = [
        t1.customerId(),
        t1.name(),
        t1."E-Mail Address".as("attribute3"),
        t1.Country.as("attribute4"),
        t1.City.as("attribute5"),
        t1.Region.as("attribute7"),
]

api.global.customers = qapi.source(t1, fields, t1.customerId().in(finalList)).stream {
    it.collect { it }
}

return null
