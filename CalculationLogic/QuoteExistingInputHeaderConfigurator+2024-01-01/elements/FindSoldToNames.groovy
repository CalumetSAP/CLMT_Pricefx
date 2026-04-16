if (api.isInputGenerationExecution()) return []

def soldToValues = out.GetSoldToValues

def customerFields = ["customerId", "name"]
def customerFilter = soldToValues ? Filter.in("customerId", soldToValues) : null

return api.stream("C", null, customerFields, customerFilter)?.withCloseable { it.collectEntries {
    [(it.customerId): it.name ?: ""]
}} ?: [:]