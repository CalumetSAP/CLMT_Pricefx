if (api.isInputGenerationExecution()) return

final calculations = libs.PricelistLib.Calculations

// Find Price List Ids with "PENDING" status in the ApprovedPriceListStatus CPT, then set "PROCESSING" status
def pricelistType = libs.PricelistLib.Constants.LIST_PRICE_ZLIS_PL_TYPE
def pendingStatus = libs.PricelistLib.Calculations.PENDING_STATUS

List<Map> pendingPriceLists = calculations.getCPTRowsByStatusAndPlTypes(pendingStatus, [pricelistType])

api.trace("pendingPL", null, pendingPriceLists)

if (!pendingPriceLists) return

// Change status to "PROCESSING" and store priceListIds to process it in batches in the next execution
pendingPriceLists.eachWithIndex { pendingPL, index ->

    calculations.setProcessingStatus(pendingPL.plId)
    dist.addOrUpdateCalcItem("batchNo-$index", pendingPL.plId, [plType: pendingPL.plType])
}