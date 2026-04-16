if (api.isInputGenerationExecution()) return

//def qapi = api.queryApi()
//def t1 = qapi.tables().companyParameterRows("PendingDivisionQuotes")
//
//return qapi.source(t1, [t1.key1(), t1.Division], t1.Status.equal("PENDING")).stream { it.collect() }
def filters = [
        Filter.equal("lookupTable.name", "PendingDivisionQuotes"),
        Filter.equal("lookupTable.status", "Active"),
        Filter.equal("attribute2", "PENDING"),
]

def fields = ["name", "attribute1"]

return api.find("MLTV", 0, 50, "lastUpdateDate", fields, *filters).collect { [
        key1    : it.name,
        Division: it.attribute1
]}