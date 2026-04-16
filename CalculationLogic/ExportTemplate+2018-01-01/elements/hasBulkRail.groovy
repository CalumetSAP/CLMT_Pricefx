if (api.isInputGenerationExecution()) return null

def quote = api.currentItem()

def showBulkRail = false
if (quote?.get("quoteType") == "New Contract" || quote?.get("quoteType") == "NewContract") {
    for (line in quote?.lineItems) {
        def configurator = line.get("inputs").find { it.name == "Inputs" }?.value ?: [:]
        def modeOfSales = configurator.SalesShippingMethodInput

        if (modeOfSales == "Bulk Rail") {
            showBulkRail = true
            break
        }
    }
}

return showBulkRail
