if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("SendEmailsByUUID")

def result = qapi.source(t1, [t1.CustomEvent, t1.UUID], t1.EmailSent.notEqual(true)).stream {
     it.collect().groupBy { it.UUID }
} ?: [:]

def customEvent
result?.each { key, values ->
    customEvent = values?.find { it.CustomEvent }?.CustomEvent as String
    if (customEvent) api.customEvent(api.jsonDecode(customEvent), "DashboardEmail")
}

return null