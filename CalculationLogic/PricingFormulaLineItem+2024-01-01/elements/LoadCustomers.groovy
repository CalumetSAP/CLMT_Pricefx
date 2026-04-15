if (api.global.isFirstRow) {
    List<Object> quotes = api.global.quotes?.collect()
    HashSet<String> customerIds = new HashSet<>()
    customerIds.addAll(quotes?.SoldTo ?: [])
    customerIds.addAll(quotes?.ShipTo ?: [])
    customerIds.remove(null)

    api.global.customersData = api.stream("C", null, ["customerId", "name", "attribute5", "attribute7"], Filter.in("customerId", customerIds))?.withCloseable {
        it.collectEntries { [(it.customerId): it] }
    }
}

return null