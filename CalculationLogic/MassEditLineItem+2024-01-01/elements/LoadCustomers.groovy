if (libs.SharedLib.BatchUtils.isNewBatch()) {
    List<Object> quotes = api.global.quotes?.collect()
    HashSet<String> customerIds = new HashSet<>()
    customerIds.addAll(quotes?.SoldTo ?: [])
    customerIds.addAll(quotes?.ShipTo ?: [])
    customerIds.remove(null)

    if (customerIds.isEmpty()) {
        api.global.customersNames = [:]
        return
    }

    def qapi = api.queryApi()
    def t1 = qapi.tables().customers()

    api.global.customersNames = qapi.source(t1, [t1.customerId(), t1.name()], t1.customerId().in(customerIds as List)).stream { it.collectEntries {
        [(it.customerId): it.name]
    } }
}

return null