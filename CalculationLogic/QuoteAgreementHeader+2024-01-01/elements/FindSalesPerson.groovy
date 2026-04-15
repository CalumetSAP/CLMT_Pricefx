if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def filter = [
        Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
        Filter.equal("attribute4", "ZS")]
def fields = ["attribute1"]

def salesPersonOptions = api.stream("CX", null, fields, *filter)?.withCloseable { it.collect { it.attribute1 } }

filter = [
        Filter.in("customerId", salesPersonOptions)
]
fields = ["customerId", "name"]
def customerMasterData = api.stream("C", null, fields, *filter)?.withCloseable { it.collect() }
def customerName

return salesPersonOptions?.collect { salesPerson ->
    customerName = customerMasterData?.find { it.customerId == salesPerson }?.name ?: ""
    return salesPerson + " - " + customerName
}