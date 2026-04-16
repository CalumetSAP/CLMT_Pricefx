if (libs.SharedLib.BatchUtils.isNewBatch()) {
    List<Object> quotes = api.global.quotes?.collect()
    HashSet<String> customerIds = new HashSet<>()
    customerIds.addAll(quotes?.SoldTo ?: [])
    customerIds.addAll(quotes?.ShipTo ?: [])
    customerIds.remove(null)

    api.global.customersNames = api.stream("C", null, ["customerId", "name"], Filter.in("customerId", customerIds))?.withCloseable {
        it.collectEntries { [(it.customerId): it.name] }
    }
}

return null