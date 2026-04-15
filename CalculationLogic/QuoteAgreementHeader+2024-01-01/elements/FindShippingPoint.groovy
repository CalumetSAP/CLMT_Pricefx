if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def fields = ["name", "attribute1"]
def filter = Filter.equal("attribute7", true)

def shippingPoints = api.findLookupTableValues(tablesConstants.SHIPPING_POINT, fields, null, filter)?.collectEntries {
    [(it.name): it.name + " - " + it.attribute1]
} ?: [:]

return shippingPoints