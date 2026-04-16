if (api.isInputGenerationExecution() || api.isDebugMode()) return

final calculations = libs.PricelistLib.Calculations

// Find Price List Ids with "PENDING" status in the ApprovedPriceListStatus CPT, then set "PROCESSING" status
List<Map> pendingPriceLists = calculations.getPendingRowsForQuoteDS()
if (!pendingPriceLists) return

// Change status to "PROCESSING" and store priceListIds to process it in batches in the next execution
pendingPriceLists.eachWithIndex { pendingPL, index ->
    calculations.setProcessingStatus(pendingPL.plId)
    dist.addOrUpdateCalcItem("batchNo-$index", pendingPL.plId, [plType: pendingPL.plType])
}