if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def filters = [
        Filter.equal("key1", "Quote")
]
def data = api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)

return groupData(data)

def groupData(data) {
    data.inject([:]) { formatted, entry ->
        String key = entry["key2"]
        def value
        if (entry["attribute1"]) {
            if (isOnlyDescriptionType(key)) {
                value = [(entry["key3"]) : (entry["attribute1"])]
            } else if (isCodeEqualToDescriptionType(key)) {
                value = [(entry["attribute1"]) : (entry["attribute1"])]
            } else {
                value = [(entry["key3"]) : (entry["key3"] + " - " + entry["attribute1"])]
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