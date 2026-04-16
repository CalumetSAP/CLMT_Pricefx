if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

def filter = [
        Filter.equal("attribute1", "ZC15")
]
def fields = ["customerId", "name"]
api.local.salesPersonTable = api.stream("C", null, fields, *filter)?.withCloseable { it.collect {
    it.customerId + (it.name ? " - " + it.name : "")
} }

return null