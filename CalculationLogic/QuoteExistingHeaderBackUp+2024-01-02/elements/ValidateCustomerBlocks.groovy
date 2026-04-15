if (!quoteProcessor.isPrePhase() || api.isInputGenerationExecution()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def shipTos = customerConfigurator?.get(headerConstants.SHIP_TO_ID)?.collect { it.split(" - ").getAt(0).trim() } ?: []

def blockFilter = Filter.or(
        Filter.and(
                Filter.isNotNull("attribute10"),
                Filter.isNotEmpty("attribute10")
        ),
        Filter.and(
                Filter.isNotNull("attribute11"),
                Filter.isNotEmpty("attribute11")
        ),
        Filter.and(
                Filter.isNotNull("attribute13"),
                Filter.isNotEmpty("attribute13")
        ),
)

def filters = Filter.and(
        blockFilter,
        Filter.in("customerId", shipTos)
)

def invalidShipTos = api.stream("C", null, ["customerId"], filters).withCloseable {
    it.collect { it.customerId }
}.unique()

api.global.invalidShipTos = invalidShipTos

return null