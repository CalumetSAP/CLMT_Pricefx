def configurator = quote?.inputs?.find { it.name == "InputsConfigurator" }?.value
//List<String> contractNumbers = configurator?.ContractPOInput?.collect { it.data.SAPContractNumber }
String salesOrg = configurator?.SalesOrgInput
String soldTo = configurator?.SoldToInput
String division = configurator?.DivisionInput

if (workflowHistory?.steps) {
    if (workflowHistory.steps.last().uniqueName == workflowHistory.activeStep.uniqueName) {
        if (workflowHistory.activeStep.approved) {
            fillCPTs(quote?.typedId, quote?.quoteType, salesOrg, division, soldTo, quote?.lineItems)
        }
    }
} else {
    fillCPTs(quote?.typedId, quote?.quoteType, salesOrg, division, soldTo, quote?.lineItems)
}

def fillCPTs (typedId, quoteType, String salesOrg, String division, String soldTo, lineItems) {
    def quoteLibraryCalculations = libs.QuoteLibrary.Calculations

    def addEmptyItem = false

    for (lineItem in lineItems) {
        def sapContractNumber = lineItem?.outputs?.find{ output -> output.resultName == "SAPContractNumber"}?.result
        def shipTo = lineItem?.outputs?.find{ output -> output.resultName == "ShipTo"}?.result?.tokenize(" - ")?.getAt(0)

        if(sapContractNumber) {
            quoteLibraryCalculations.addContractUUID(typedId, sapContractNumber)
        } else if(shipTo) {
            def key = """${soldTo}-${shipTo}-${salesOrg}-${division}"""
            quoteLibraryCalculations.addContractUUID(typedId, key)
        } else {
            addEmptyItem = true
        }
    }

    if(addEmptyItem){
        quoteLibraryCalculations.addContractUUID(typedId)
    }

    quoteLibraryCalculations.addOrUpdateStatusToPending(typedId, quoteType)
    quoteLibraryCalculations.addOrUpdateQuoteForScalesStatusToPending(typedId)

    if (quoteType == "ExistingContractUpdate") quoteLibraryCalculations.addQuotesForCRStatusToPending([typedId])
}