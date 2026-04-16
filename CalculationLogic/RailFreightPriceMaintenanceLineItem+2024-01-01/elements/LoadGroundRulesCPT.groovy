if (api.global.isFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("GroundRules")
    def fields = [t1.key1(), t1.key2(), t1.key3(), t1.key4(), t1.DeliveredPrice, t1.AdjustmentFactor]

    def rows = qapi.source(t1, fields).stream {it.collect {it } } ?: []

    def groundRulesMap = [:]

    def freightCondMap, freightChangeMap, conditionTypeMap
    rows.each {
        freightCondMap = groundRulesMap[it.key2] ?: (groundRulesMap[it.key2] = [:])
        freightChangeMap = freightCondMap[it.key3] ?: (freightCondMap[it.key3] = [:])
        conditionTypeMap = freightChangeMap[it.key1] ?: (freightChangeMap[it.key1] = [:])

        conditionTypeMap[it.key4] = it
    }

    api.global.groundRules = groundRulesMap
}

return null