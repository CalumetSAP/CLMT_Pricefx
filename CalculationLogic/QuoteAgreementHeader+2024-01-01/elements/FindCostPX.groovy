if (api.isInputGenerationExecution() || quoteProcessor.isPrePhase() || !api.local.lineItemPlants) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final calculations = libs.QuoteLibrary.Calculations

def filter = [
        Filter.equal("name", tablesConstants.PRODUCT_EXTENSION_COST),
        Filter.in("sku", api.local.lineItemSkus),
        Filter.in("attribute2", calculations.removePlantDescription(api.local.lineItemPlants))]
def fields = ["sku", "attribute2", "attribute3", "attribute5"]

return api.stream("PX", "-lastUpdateDate", fields, *filter)?.withCloseable { it.collectEntries {
    [(it.sku + "|" + it.attribute2): [
            CostingLotSize: it.attribute3,
            StandardPrice : it.attribute5,
    ]]
}}