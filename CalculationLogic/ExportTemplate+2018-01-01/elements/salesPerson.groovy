if (api.isInputGenerationExecution()) return null

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def quote = api.currentItem()

def salesPerson = null
if (quote?.get("quoteType") == "New Contract" || quote?.get("quoteType") == "NewContract") {
    for (line in quote?.lineItems) {
        if (!line.folder) {
            def configurator = line.get("inputs").find { it.name == "Inputs" }?.value ?: [:]
            salesPerson = configurator.SalesPersonInput?.split("-")?.getAt(1)?.trim()
            break
        }
    }
} else if (quote?.get("quoteType") == "ExistingContractUpdate") {
    for (line in quote?.lineItems) {
        if (!line.folder) {
            def salesPersonAux = calculations.getInputValue(line, lineItemConstants.SALES_PERSON_ID)?.split("-")
            if (salesPersonAux?.size() < 2) continue
            salesPerson = salesPersonAux?.getAt(1)?.trim()
            break
        }
    }
}


return salesPerson ?: ""