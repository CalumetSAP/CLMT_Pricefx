if (api.isInputGenerationExecution() || (!api.local.lineItemShipTos && !api.local.addedContracts)) return

Set<String> shipToSet = [] as Set
if (api.local.lineItemShipTos) {
    shipToSet.addAll(api.local.lineItemShipTos)
}
if (api.local.addedContracts) {
    (out.FindContractDSData ?: [:]).each { _, lines ->
        lines?.each { shipToSet << it?.ShipTo }
    }
}
if (shipToSet.isEmpty()) return [:]

def qapi = api.queryApi()

def t1 = qapi.tables().customers()

def fields = [
        t1.customerId(),
        t1.name(),
        t1.Country,
        t1.City,
        t1.PostalCode,
        t1.Region,
        t1.Street,
        t1.Industry
]

def shipToOutputData = [:]

def shipTos = qapi.source(t1, fields, t1.customerId().in(shipToSet.toList())).stream {
    it.collectEntries {
        shipToOutputData.put(it.customerId, [
                Industry: it.Industry,
                Address : it.Street,
                City    : it.City,
                State   : it.Region,
                Zip     : it.PostalCode,
                Country : it.Country,
        ])
        [(it.customerId): it.customerId + " - " + (it.name ?: "") + " - " + (it.City ?: "") + " - " + (it.Region ?: "")]
    }
} ?: [:]

api.local.shipToOutputData = shipToOutputData

return shipTos