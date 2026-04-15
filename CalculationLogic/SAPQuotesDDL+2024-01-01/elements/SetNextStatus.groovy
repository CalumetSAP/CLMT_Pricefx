if (api.isInputGenerationExecution() || api.isDebugMode()) return

final customEvents = libs.QuoteLibrary.CustomEvent
final quoteCalculations = libs.QuoteLibrary.Calculations

def processingSAPQuotes = quoteCalculations.getProcessingSAPQuotesRows()
if (processingSAPQuotes) {
    processingSAPQuotes.each { processingQuote ->
        customEvents.addNewQuoteCustomEvent(quoteCalculations.getNewQuoteUUID(processingQuote.quoteId))
    }
    quoteCalculations.addOrUpdateStatusToWaitingSAPUpdateQuotes(processingSAPQuotes.quoteId)
}

return null