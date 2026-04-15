if (!quoteProcessor.isPrePhase() || api.isInputGenerationExecution()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def shipTos = customerConfigurator?.get(headerConstants.SHIP_TO_ID)?.collect { it.split(" - ").getAt(0).trim() } ?: []

if (!shipTos) return

def qapi = api.queryApi()

def t1 = qapi.tables().customers()

def filter = qapi.exprs().and(
        qapi.exprs().or(
                qapi.exprs().and(
                        t1.CustomerOrderBlock.notEqual(""),
                        t1.CustomerOrderBlock.isNotNull(),
                ),
                qapi.exprs().and(
                        t1.CustomerDeliveryBlock.notEqual(""),
                        t1.CustomerDeliveryBlock.isNotNull(),
                ),
                qapi.exprs().and(
                        t1.BillingBlock.notEqual(""),
                        t1.BillingBlock.isNotNull(),
                ),

        ),
        t1.customerId().in(shipTos)
)

def invalidShipTos = qapi.source(t1, [t1.customerId()], filter).stream { it.collect { it.customerId } } ?: []

api.global.invalidShipTos = invalidShipTos?.unique()

return null