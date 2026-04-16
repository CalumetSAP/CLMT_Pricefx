if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("UOMDescription")
def fields = [
        t1.key1().as("Name"),
        t1."UOM Description"
]

api.global.uomDescription = qapi.source(t1, fields).stream { it.collectEntries {
    [(it.Name): it."UOM Description"]
}}

return null