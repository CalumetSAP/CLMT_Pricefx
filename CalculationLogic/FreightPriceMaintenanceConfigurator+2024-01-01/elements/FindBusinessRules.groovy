if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("FreightBusinessRules")
def filter = qapi.exprs().and(
        t1.key1().in(["ExpirationDateDefaultByMode", "ConditionType"])
)

def rows = qapi.source(t1, [t1.key1(), t1.key2(), t1.key3(), t1.Description], filter).stream { it.collect {it } }

def result = [:].withDefault { [:] }

rows.each { r ->
    switch (r.key1) {
        case "ExpirationDateDefaultByMode":
        case "ConditionType":
            def desc = r.Description ? " - " + r.Description : ""
            result[r.key1][r.key2] = r.key3 + desc
            break
    }
}

return result