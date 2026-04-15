if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def sku = InputMaterial?.input?.getValue()

def filter = [
        Filter.equal("name", tablesConstants.PRODUCT_EXTENSION_COST),
        Filter.equal("sku", sku)]
def fields = ["sku", "attribute2", "attribute3", "attribute5", "attribute9"]

api.local.costPX = api.stream("PX", "-lastUpdateDate", fields, *filter)?.withCloseable { it.collectEntries {
    [(it.sku + "|" + it.attribute2): [
            CostingLotSize: it.attribute3,
            StandardPrice : it.attribute9,
            SecondayPrice : it.attribute5,
    ]]
}}

return null