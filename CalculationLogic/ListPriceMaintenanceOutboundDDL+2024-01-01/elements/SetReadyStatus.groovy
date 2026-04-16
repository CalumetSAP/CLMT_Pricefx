if (api.isInputGenerationExecution() || api.isDebugMode()) return

final calculations = libs.PricelistLib.Calculations

List<Object> processingPriceLists = calculations.getProcessingRowsForBasePricingDS()
if (!processingPriceLists) return

//Change all "PROCESSING" price lists to "READY" status
processingPriceLists.each { processingPL ->
    calculations.setReadyStatus(processingPL.plId)
}