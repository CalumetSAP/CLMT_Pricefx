if (api.isInputGenerationExecution() || api.isDebugMode()) return

final calculations = libs.QuoteLibrary.Calculations

// Find Quote Ids with "WAITING SAP UPDATE" or "RE-WAITING SAP UPDATE" status in the ApprovedQuoteStatus CPT, then set "PROCESSING SAP UPDATE" status
def waitingSAPUpdateQuotes = calculations.getWaitingOrReWaitingSAPUpdateRows()
if (waitingSAPUpdateQuotes) {
    calculations.addOrUpdateStatusToProcessingSAPUpdateQuotes(waitingSAPUpdateQuotes.quoteId)

    // Store quoteIds in the CPT to process it in the next execution
    waitingSAPUpdateQuotes.eachWithIndex { quote, index ->
        dist.addOrUpdateCalcItem("batchNo-$index", quote.quoteId, [quoteType: quote.quoteType])
    }
}

return null