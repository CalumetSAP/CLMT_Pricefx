if (libs.SharedLib.BatchUtils.isNewBatch()) {
    List<String> customers = api.global.shipToList as List

    if (customers) {
        def qapi = api.queryApi()

        def t1 = qapi.tables().customers()
        def fields = [t1.customerId(), t1.name(), t1.PostalCode, t1.Country]

        api.global.customers = qapi.source(t1, fields, t1.customerId().in(customers))
                .stream {
                    it.collectEntries {
                        [(it.customerId): [
                                Name      : it.name,
                                PostalCode: beforeDash(it.PostalCode as String),
                                Country   : it.Country
                        ]]
                    }
                } ?: [:]
    } else {
        api.global.customers = [:]
    }
}

return api.global.customers.get(out.LoadQuotes.ShipTo) ?: [:]

String beforeDash(String value) {
    if (!value) return null
    int i = value.indexOf('-')
    return (i >= 0) ? value.substring(0, i) : value
}