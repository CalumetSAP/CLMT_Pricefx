if (api.isInputGenerationExecution() || api.isDebugMode()) return

final calculations = libs.PricelistLib.Calculations

// Find Price List Ids with "PENDING" status in the ApprovedPriceListStatusForBasePricing CPT, then set "PROCESSING" status
List pendingPLIds = calculations.getPendingPriceListIdsForBasePricing()
if (!pendingPLIds) return

// Change status to "PROCESSING" and store priceListIds to process it in batches in the next execution
calculations.addOrUpdatePriceListForBasePricingStatusToProcessing(pendingPLIds)
pendingPLIds.eachWithIndex { pendingPLId, index ->
    dist.addOrUpdateCalcItem("batchNo-$index", pendingPLId, [plId: pendingPLId])
}