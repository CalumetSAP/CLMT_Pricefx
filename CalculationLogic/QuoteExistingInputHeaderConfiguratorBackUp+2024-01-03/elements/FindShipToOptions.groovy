if (api.isInputGenerationExecution()) return []

def soldToValues = out.GetSoldToValues

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def filter = [
        Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
        Filter.in("attribute4", ["SH", "WE"])]
def fields = ["attribute1"]

if (soldToValues) filter.add(Filter.in("customerId", soldToValues))

def customerIds = api.stream("CX", null, fields, true, *filter)?.withCloseable { it.collect { it.attribute1 } }

filter = [Filter.in("customerId", customerIds)]
fields = ["customerId", "name", "attribute5", "attribute7"]

return api.stream("C", null, fields, *filter)?.withCloseable { it.collectEntries {
    [(it.customerId): it.customerId + " - " + (it.name ?: "") + " - " + (it.attribute5 ?: "") + " - " + (it.attribute7 ?: "")]
}} ?: [:]