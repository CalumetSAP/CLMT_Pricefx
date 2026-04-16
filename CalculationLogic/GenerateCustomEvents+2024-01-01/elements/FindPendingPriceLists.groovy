if (api.isInputGenerationExecution()) return

api.local.pendingPriceListIds = libs.PricelistLib.Calculations.getPendingCustomEventPriceListIds(api.local.lastUpdateDateFilter)

return