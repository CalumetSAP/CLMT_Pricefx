if (api.isInputGenerationExecution() || api.isDebugMode()) return

final calculations = libs.QuoteLibrary.Calculations

// Find Quote Ids with "PENDING" status in the ApprovedQuoteStatus CPT, then set "PROCESSING" status
def pendingQuotes = calculations.getPendingQuotesRows()
if (!pendingQuotes) return

def pendingExistingQuotes = pendingQuotes?.findAll { it.quoteType == "ExistingContractUpdate"}
def pendingNewQuotes = pendingQuotes?.findAll { it.quoteType == "New Contract"}

Map finalizeQuoteCFOs = api.find("CFO",0,api.getMaxFindResultsLimit(), "-lastUpdateDate", Filter.in("parentTypedId", pendingQuotes.quoteId))
        ?.groupBy {
            it.parentTypedId
        }
        ?.collectEntries { key, values ->
            [(key): values?.find()?.inputs?.find { it.name == "FinalizeQuoteInput" }?.value]
        }
        ?: [:]

//Only process quotes that have the "Finalize quote" checkbox selected
pendingNewQuotes = pendingNewQuotes.findAll { finalizeQuoteCFOs[it.quoteId] }

pendingQuotes = pendingExistingQuotes + pendingNewQuotes
if (!pendingQuotes) return

calculations.addOrUpdateStatusToProcessing(pendingQuotes.quoteId)

// Store quoteIds in the CPT to process it in the next execution
pendingQuotes.eachWithIndex { quote, index ->
    dist.addOrUpdateCalcItem("batchNo-$index", quote.quoteId, [quoteType: quote.quoteType])
}