if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows(tablesConstants.DROPDOWN_OPTIONS)

def filter = qapi.exprs().and(
        t1.key1().equal("Quote")
)

def data = qapi.source(t1, [t1.key2(), t1.key3(), t1."Attribute 1"], filter).stream { it.collect { it } } ?: []

return groupData(data)

def groupData(data) {
    data.inject([:]) { formatted, entry ->
        String key = entry["key2"]
        def value
        if (entry["Attribute 1"]) {
            if (isOnlyDescriptionType(key)) {
                value = [(entry["key3"]) : (entry["Attribute 1"])]
            } else if (isCodeEqualToDescriptionType(key)) {
                value = [(entry["Attribute 1"]) : (entry["Attribute 1"])]
            } else {
                value = [(entry["key3"]) : (entry["key3"] + " - " + entry["Attribute 1"])]
            }
        } else {
            value = [(entry["key3"]) : (entry["key3"])]
        }
        formatted[key] = formatted.containsKey(key) ? formatted[key] + value : value
        formatted
    }
}

def isOnlyDescriptionType(key) {
    return ["PriceType", "ReferencePeriod", "Currency", "PriceProtection", "FreightTerm"].contains(key)
}

def isCodeEqualToDescriptionType(key) {
    return ["RecalculationPeriod", "MovementTiming"].contains(key)
}