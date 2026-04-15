if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("Plant")
def fields = [
        t1.key1().as("Name"),
        t1."Attribute 10",
        t1."Attribute 18",
]

api.global.plant = qapi.source(t1, fields).stream { it.collectEntries {
    [(it.Name): [it."Attribute 10", it."Attribute 18"].join(", ")]
}}

return null