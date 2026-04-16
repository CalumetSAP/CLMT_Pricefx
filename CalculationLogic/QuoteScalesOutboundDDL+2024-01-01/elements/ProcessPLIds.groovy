if (api.isInputGenerationExecution() || api.isDebugMode()) return

final calculations = libs.QuoteLibrary.Calculations

// Find Quote Ids with "PENDING" status in the ApprovedQuoteStatusForScales CPT, then set "PROCESSING" status
List<Object> quoteIds = calculations.getQuoteIdsForScalesByStatus(calculations.PENDING_STATUS)
if (!quoteIds) return

calculations.addOrUpdateQuoteForScalesStatusToProcessing(quoteIds)

// Store quoteIds to process it in batches in the next execution
quoteIds.eachWithIndex { quoteId, i ->
    dist.addOrUpdateCalcItem("batchNo-$i", quoteId, [:])
}