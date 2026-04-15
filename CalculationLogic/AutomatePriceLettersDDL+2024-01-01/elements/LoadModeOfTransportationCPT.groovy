if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("ModeofTransportation")
def fields = [
        t1.key1().as("Name"),
        t1.Description
]

api.global.modeOfTransportation = qapi.source(t1, fields).stream { it.collectEntries {
    [(it.Name): it.Description]
}}

return null