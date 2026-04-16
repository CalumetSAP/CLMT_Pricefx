if (api.isInputGenerationExecution() || api.isDebugMode()) return

final calculations = libs.QuoteLibrary.Calculations

// Find Quote Ids with "PENDING SAP QUOTES" status in the ApprovedQuoteStatus CPT, then set "PROCESSING SAP QUOTES" status
def pendingSAPQuotes = calculations.getPendingSAPQuotesRows()
if (pendingSAPQuotes) {
    calculations.addOrUpdateStatusToProcessingSAPQuotes(pendingSAPQuotes.quoteId)

    // Store quoteIds in the CPT to process it in the next execution
    pendingSAPQuotes.eachWithIndex { quote, index ->
        dist.addOrUpdateCalcItem("batchNo-$index", quote.quoteId, [quoteType: quote.quoteType])
    }
}

return null