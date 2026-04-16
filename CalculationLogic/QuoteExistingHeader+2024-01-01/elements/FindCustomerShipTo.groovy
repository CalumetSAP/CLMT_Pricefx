if (api.isInputGenerationExecution() || (!api.local.lineItemShipTos && !api.local.addedContracts)) return

def shipToList = []
if (api.local.lineItemShipTos) {
    shipToList = api.local.lineItemShipTos
} else {
    def contracts = out.FindContractDSData
    contracts?.each { contractNumber, lines ->
        lines?.each { line ->
            shipToList.add(line?.ShipTo)
        }
    }
}
shipToList = shipToList?.unique()

def filter = Filter.in("customerId", shipToList)
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