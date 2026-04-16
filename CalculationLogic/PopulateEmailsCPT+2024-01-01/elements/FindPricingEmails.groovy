if (api.isInputGenerationExecution()) return

def mapDivision = [
        "20": "SPS",
        "30": "PB"
]

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("WorkflowEmails")

api.global.pricingEmails = qapi.source(t1, [t1.Division, t1.Email], t1.Step.equal("Pricing")).stream {
    it.collectEntries {
        [(mapDivision[it.Division]): it.Email]
    }
} ?: [:]

return null