if (api.isInputGenerationExecution() || !api.local.lineItemShipTos) return

def filter = Filter.in("customerId", api.local.lineItemShipTos as List)
def fields = ["customerId", "name", "attribute4", "attribute5", "attribute6", "attribute7", "attribute8", "attribute12"]

def shipToOutputData = [:]

def shipTos = api.stream("C", null, fields, filter)?.withCloseable {
    it.collectEntries {
        shipToOutputData.put(it.customerId, [
                Industry: it.attribute12,
                Address : it.attribute8,
                City    : it.attribute5,
                State   : it.attribute7,
                Zip     : it.attribute6,
                Country : it.attribute4,
        ])
        [(it.customerId): it.customerId + " - " + (it.name ?: "") + " - " + (it.attribute5 ?: "") + " - " + (it.attribute7 ?: "")]
    }
}

api.local.shipToOutputData = shipToOutputData

return shipTos