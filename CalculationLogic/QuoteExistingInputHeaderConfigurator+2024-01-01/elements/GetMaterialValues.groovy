if (api.isInputGenerationExecution()) return

def selectedMaterial = InputMaterial.input?.getValue()

if (selectedMaterial instanceof List) return selectedMaterial

if (selectedMaterial?.productFieldValue != null) {
    return [selectedMaterial?.productFieldValue]
} else if (selectedMaterial?.productFilterCriteria != null) {
    def materialFilter = api.filterFromMap(selectedMaterial?.productFilterCriteria)
    return api.find("P", 0, api.getMaxFindResultsLimit(), "sku", ["sku"], true, materialFilter)?.collect { it.sku }
} else {
    return []
}