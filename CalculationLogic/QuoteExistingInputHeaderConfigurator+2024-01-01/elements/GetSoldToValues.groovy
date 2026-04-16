if (api.isInputGenerationExecution()) return

def selectedSoldTo = InputSoldTo.input?.getValue()

if (selectedSoldTo instanceof List) return selectedSoldTo

if (selectedSoldTo?.customerFieldValue != null) {
    return [selectedSoldTo?.customerFieldValue]
} else if (selectedSoldTo?.customerFilterCriteria != null) {
    def customerFilter = api.filterFromMap(selectedSoldTo?.customerFilterCriteria)
    return api.find("C", 0, api.getMaxFindResultsLimit(), "customerId", ["customerId"], true, customerFilter)?.collect { it.customerId }
} else {
    return []
}