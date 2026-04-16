if (api.global.isFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("ShippingPoint")

    api.global.shippingPoints = qapi.source(t1, [t1.key1(), t1.ShippingPointZIp], t1.Active.equal(true)).stream { it.collectEntries {
        [(it.key1): it.ShippingPointZIp]
    } }
}