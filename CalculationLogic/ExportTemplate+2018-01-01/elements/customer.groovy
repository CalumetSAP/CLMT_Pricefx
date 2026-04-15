if (api.isInputGenerationExecution()) return null

def quote = api.currentItem()

def soldToData = null
if (quote?.get("quoteType") == "New Contract" || quote?.get("quoteType") == "NewContract") {
    def soldToInput = quote.get("inputs").find { it.name == "InputsConfigurator" }?.value?.SoldToInput
    def soldToFilters = [
            Filter.equal("customerId", soldToInput),
    ]
    soldToData = api.find("C", 0, 1, null, ["customerId", "name"], *soldToFilters)?.getAt(0)
} else if (quote?.get("quoteType") == "ExistingContractUpdate") {
    def soldToInput = quote.get("inputs").find { it.name == "InputsConfigurator" }?.value?.SoldToInput?.find()
    def soldToFilters = [
            Filter.equal("customerId", soldToInput),
    ]
    soldToData = api.find("C", 0, 1, null, ["customerId", "name"], *soldToFilters)?.getAt(0)
}

return soldToData ? "${soldToData.customerId} ${soldToData.name}" : ""
