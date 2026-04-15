if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("DashboardFooters")
def fields = [
        t1.key1(),
        t1.key2(),
        t1.key3(),
        t1.Text.as("attribute1")
]

def records = qapi.source(t1, fields).stream {
    it.collect()?.inject([:]) { acc, item ->
        def mapKey = item.key1 + "|" + item.key2
        def list = acc.get(mapKey, [])
        list << [order: item.key3, value: item.attribute1]
        acc[mapKey] = list
        return acc
    }
} ?: [:]

records = records.collectEntries { k, v ->
    [k, v.sort { it.order }]
}

api.global.footers = records

return null