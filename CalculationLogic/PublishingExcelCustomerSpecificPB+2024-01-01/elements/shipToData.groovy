if (api.isInputGenerationExecution() || !api.local.shipTo) {
    def data = []

    data.add(["Title": "Ship To Name", "Value": null])
    data.add(["Title": "Address", "Value": null])
    data.add(["Title": "City", "Value": null])
    data.add(["Title": "State", "Value": null])
    data.add(["Title": "ZIP", "Value": null])
    data.add(["Title": "Country", "Value": null])

    return data
}

def qapi = api.queryApi()
def t1 = qapi.tables().customers()
def fields = [t1.customerId(), t1.name(), t1.Street, t1.City, t1.Region, t1.PostalCode, t1.Country]

def result = qapi.source(t1, fields, t1.customerId().in(api.local.shipTo)).stream {
    it.collect { it }
}

def data = []

data.add(["Title": "Ship To Name", "Value": result?.name?.toList()?.join(", ")])
data.add(["Title": "Address", "Value": result?.Street?.toList()?.join(", ")])
data.add(["Title": "City", "Value": result?.City?.toList()?.join(", ")])
data.add(["Title": "State", "Value": result?.Region?.toList()?.join(", ")])
data.add(["Title": "ZIP", "Value": result?.PostalCode?.toList()?.join(", ")])
data.add(["Title": "Country", "Value": result?.Country?.toList()?.join(", ")])

return data