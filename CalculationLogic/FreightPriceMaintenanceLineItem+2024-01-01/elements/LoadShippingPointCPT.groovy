if (api.global.isFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("ShippingPoint")

    api.global.shippingPoints = qapi.source(t1, [t1.key1(), t1.Description, t1.ShippingPointZIp, t1.OverheadAssesorialPercentTruck], t1.Active.equal(true)).stream {
        it.collectEntries {
            [(it.key1): [
                    Description                    : it.Description,
                    ZIP                            : beforeDash(it.ShippingPointZIp as String),
                    OverheadAssessorialPercentTruck: it.OverheadAssesorialPercentTruck,
            ]]
        }
    } ?: [:]
}

return api.global.shippingPoints.get(out.LoadQuotes.ShippingPoint) ?: [:]

String beforeDash(String value) {
    if (!value) return null
    int i = value.indexOf('-')
    return (i >= 0) ? value.substring(0, i) : value
}