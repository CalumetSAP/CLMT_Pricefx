if (api.isInputGenerationExecution()) return [:]

def qapi = api.queryApi()
def t1 = qapi.tables().customers()

return qapi.source(t1, [t1.customerId(), t1.name()], t1.AccountGroup.equal("ZC15"))
        .stream { it.collectEntries {[(it.customerId) : it.customerId + (it.name ? " - " + it.name : "")] } } ?: []
