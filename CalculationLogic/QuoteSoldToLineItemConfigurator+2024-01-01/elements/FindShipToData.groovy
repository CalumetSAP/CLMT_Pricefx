if (api.isInputGenerationExecution()) return

def shipToList = api.local.shipToLines?.ShipTo ?: []
def shipToOptions = api.local.shipToOptions ? api.local.shipToOptions?.collect { it?.toString()?.split(" - ")?.getAt(0) } : []
shipToList.addAll(shipToOptions)

def filter = Filter.in("customerId", shipToList.unique() as List)
def fields = ["customerId", "name", "attribute4", "attribute5", "attribute6", "attribute7", "attribute8", "attribute12"]

def shipToData = api.stream("C", null, fields, filter)?.withCloseable {
    it.collectEntries {
        [(it.customerId): [
                ShipToName: it.customerId + (it.name ? " - " + it.name : ""),
                Industry  : it.attribute12,
                Address   : it.attribute8,
                City      : it.attribute5,
                State     : it.attribute7,
                Zip       : it.attribute6,
                Country   : it.attribute4,
        ]]
    }
}

return shipToData ?: [:]