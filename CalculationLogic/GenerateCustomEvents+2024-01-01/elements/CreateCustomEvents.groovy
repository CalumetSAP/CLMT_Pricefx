if (api.isInputGenerationExecution()) return

final customEvents = libs.QuoteLibrary.CustomEvent
final quoteCalculations = libs.QuoteLibrary.Calculations
final priceListCalculations = libs.PricelistLib.Calculations
final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

List readyIds = []

api.local.pendingQuotes.each { pendingQuote ->
    def quote = api.getCalculableLineItemCollection(pendingQuote.quoteId)
    if (pendingQuote.quoteType == "New Contract") {
        readyIds.add(pendingQuote.quoteId)
        uuids = quoteCalculations.getNewQuoteUUIDs(pendingQuote.quoteId)

        for(uuid in uuids) {
            customEvents.addNewQuoteCustomEvent(uuid)
        }
    } else {
        def contractUUIDs = quoteCalculations.getUUIDsById(pendingQuote.quoteId)

        def quoteHeaderConfigurator = quote.inputs ?.find{ it.name == headerConstants.INPUTS_NAME }?.value

        contractUUIDs?.each {contract ->
            def linesWithPriceChanges = quote?.lineItems?.findAll {
                getInputByName(it?.inputs, lineItemInputsConstants.PRICE_CHANGE_FLAG_ID) && getOutputByName(it?.outputs, "SAPContractNumber") == contract?.key2
            }?.size()
            def linesWithChanges = quote?.lineItems?.findAll {
                getInputByName(it?.inputs, lineItemInputsConstants.LINE_HAS_CHANGED_ID) && getOutputByName(it?.outputs, "SAPContractNumber") == contract?.key2
            }?.size()
            def linesWithRejection = quote?.lineItems?.find { getInputByName(it?.inputs, lineItemInputsConstants.REJECTION_REASON_HAS_CHANGED_ID) }
            def linesWithSAPChanges = quote?.lineItems?.find { getInputByName(it?.inputs, lineItemInputsConstants.SAP_CHANGES_FLAG_ID) }
            def headerHasChange = quoteHeaderConfigurator?.get(headerConstants.HEADER_HAS_CHANGED_ID)
            def isHeaderOnly = headerHasChange instanceof Boolean ? headerHasChange : headerHasChange?.get(contract.key2)
            def freightTermHasChanged = quote?.lineItems?.find { getInputByName(it?.inputs, lineItemInputsConstants.FREIGHT_TERM_CHANGE_FLAG_ID) }

            if (linesWithPriceChanges != linesWithChanges || isHeaderOnly || linesWithRejection || linesWithSAPChanges || freightTermHasChanged) {
                customEvents.addExistingQuoteCustomEvent(contract?.attribute1)
            }
        }

        readyIds.add(pendingQuote.quoteId)
    }
}

quoteCalculations.addOrUpdateStatusToReady(readyIds)


api.local.pendingPriceListIds.each { pendingPlId ->
    priceListCalculations.setReadyStatus(pendingPlId)
    priceListCalculations.getUUIDsById(pendingPlId)?.each { uuid ->
        customEvents.addPriceMaintenanceCustomEvent(uuid)
    }
}

return

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}

def getOutputByName(outputs, name) {
    return outputs?.find { it.resultName == name}?.result
}