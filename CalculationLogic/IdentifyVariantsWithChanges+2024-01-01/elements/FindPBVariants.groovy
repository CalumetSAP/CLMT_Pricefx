if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows("VariantsPBPricePublishingDashboard")

def decodeIfJson = { v ->
    if (!(v instanceof String)) return v
    def s = v.trim()
    try {
        if (s.startsWith("[") && s.endsWith("]")) {
            return api.jsonDecodeList(s)
        }
        if (s.startsWith("{") && s.endsWith("}")) {
            return api.jsonDecode(s)
        }
    } catch (ignored) {}
    return v
}

return qapi.source(t1).stream { rows ->
    rows.collect { row ->
        row.collectEntries { k, v ->
            [(k): decodeIfJson(v)]
        }
    }
}