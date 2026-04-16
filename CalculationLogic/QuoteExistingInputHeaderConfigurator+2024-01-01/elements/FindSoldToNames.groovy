if (api.isInputGenerationExecution()) return []

def soldToValues = out.GetSoldToValues

def qapi = api.queryApi()
def t1 = qapi.tables().customers()
def customerFields = [
        t1.customerId(),
        t1.name()
]
def customerFilter = soldToValues ? t1.customerId().in(soldToValues) : t1.customerId().isNotNull()

return qapi.source(t1, customerFields, customerFilter).stream {
    it.collectEntries {
        [(it.customerId): it.name ?: ""]
    }
} ?: [:]