if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final tablesConstants = libs.QuoteConstantsLibrary.Tables

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def shipToOptions = customerConfigurator?.get(headerConstants.SHIP_TO_ID)
def selectedSoldTo = customerConfigurator?.get(headerConstants.SOLD_TO_ID)
def selectedDivision = customerConfigurator?.get(headerConstants.DIVISION_ID)
def selectedSalesOrg = customerConfigurator?.get(headerConstants.SALES_ORG_ID)

if (!shipToOptions && selectedSoldTo && selectedDivision && selectedSalesOrg) {
    def filter = [
            Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
            Filter.equal("customerId", selectedSoldTo),
            Filter.equal("attribute3", selectedDivision),
            Filter.equal("attribute6", selectedSalesOrg),
            Filter.in("attribute4", ["SH", "WE"])
    ]
    def fields = ["attribute1"]

    def customerIds = api.stream("CX", null, fields, *filter)?.withCloseable { it.collect { it.attribute1 } }?.unique()

    filter = [Filter.in("customerId", customerIds)]
    fields = ["customerId", "name"]

    shipToOptions = api.stream("C", null, fields, *filter)?.withCloseable { it.collect { it.customerId + " - " + (it.name ?: "") } }
}

return shipToOptions