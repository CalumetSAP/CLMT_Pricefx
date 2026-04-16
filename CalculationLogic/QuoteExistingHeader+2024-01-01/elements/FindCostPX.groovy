if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def qapi = api.queryApi()
def t1 = qapi.tables().productExtensionRows(tablesConstants.PRODUCT_EXTENSION_COST)

return qapi.source(t1, [t1.sku(), t1."Attribute 2", t1."Attribute 3", t1."Attribute 5", t1."Planned Price 2"], t1.sku().in(api.local.lineItemSkus))
        .stream { it.collectEntries {
            [(it.sku + "|" + it."Attribute 2"): [
                    CostingLotSize: it."Attribute 3" instanceof String ? it."Attribute 3"?.replace(",", "")?.toBigDecimal() : it."Attribute 3"?.toBigDecimal(),
                    StandardPrice : it."Planned Price 2" instanceof String ? it."Planned Price 2"?.replace(",", "")?.toBigDecimal() : it."Planned Price 2"?.toBigDecimal(),
                    SecondayPrice : it."Attribute 5" instanceof String ? it."Attribute 5"?.replace(",", "")?.toBigDecimal() : it."Attribute 5"?.toBigDecimal(),
            ]]
}} ?: [:]