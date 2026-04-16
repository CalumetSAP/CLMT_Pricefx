if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def filter = [
        Filter.equal("name", tablesConstants.PRODUCT_EXTENSION_COST),
        Filter.in("sku", api.local.lineItemSkus)]
def fields = ["sku", "attribute2", "attribute3", "attribute5", "attribute9"]

return api.stream("PX", "-lastUpdateDate", fields, *filter)?.withCloseable { it.collectEntries {
    [(it.sku + "|" + it.attribute2): [
            CostingLotSize: it.attribute3,
            StandardPrice : it.attribute9,
            SecondayPrice : it.attribute5,
    ]]
}}