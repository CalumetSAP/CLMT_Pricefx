if (!quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedSoldTo = headerConfigurator?.get(headerConstants.SOLD_TO_ID) ?: []

if (!selectedSoldTo) return

def qapi = api.queryApi()
def t1 = qapi.tables().customers()

return qapi.source(t1, [t1.Industry], t1.customerId().in(selectedSoldTo)).stream { it.collect {it.Industry }.find() }