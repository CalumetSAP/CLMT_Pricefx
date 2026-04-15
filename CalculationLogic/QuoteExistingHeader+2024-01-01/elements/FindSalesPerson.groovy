if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

def qapi = api.queryApi()

def t1 = qapi.tables().customers()

api.local.salesPersonTable = qapi.source(t1, [t1.customerId(), t1.name()], t1.AccountGroup.equal("ZC15"))
        .stream { it.collect {it.customerId + (it.name ? " - " + it.name : "") } } ?: []

return null