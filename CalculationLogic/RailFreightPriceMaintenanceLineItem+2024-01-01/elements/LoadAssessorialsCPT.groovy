if (api.global.isFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("AssessorialTruckFreight")
    def fields = [t1.key1(), t1.key2(), t1.key3(), t1.Rate]

    def rows = qapi.source(t1, fields).stream {it.collect {it } } ?: []

    def assessorialsMap = [:]

    rows.each {
        if (it.key1 == "FSC") {
            if (!assessorialsMap.containsKey(it.key1)) assessorialsMap[it.key1] = [:]
            assessorialsMap[it.key1][it.key2] = it
        } else if (it.key1.toUpperCase().contains("BORDER")) {
            if (!assessorialsMap.containsKey(it.key1)) assessorialsMap[it.key1] = [:]
            assessorialsMap[it.key1][it.key3] = it
        } else {
            if (!assessorialsMap.containsKey(it.key1)) assessorialsMap[it.key1] = [:]
            assessorialsMap[it.key1][it.key2] = it
        }
    }

    api.global.assessorials = assessorialsMap
}

return null