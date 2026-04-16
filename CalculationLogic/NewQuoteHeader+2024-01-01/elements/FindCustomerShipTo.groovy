if (api.isInputGenerationExecution() || !api.local.lineItemSkus) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final tablesConstants = libs.QuoteConstantsLibrary.Tables

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def shipToOptions = headerConfigurator?.get(headerConstants.SHIP_TO_ID)
def selectedSoldTo = headerConfigurator?.get(headerConstants.SOLD_TO_ID)
def selectedDivision = headerConfigurator?.get(headerConstants.DIVISION_ID)
def selectedSalesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)

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

    filter = [
            Filter.in("customerId", customerIds),
            Filter.or(
                    Filter.isNull("attribute10"),
                    Filter.isEmpty("attribute10")
            ),
            Filter.or(
                    Filter.isNull("attribute11"),
                    Filter.isEmpty("attribute11")
            ),
            Filter.or(
                    Filter.isNull("attribute13"),
                    Filter.isEmpty("attribute13")
            )
    ]
    fields = ["customerId", "name", "attribute5", "attribute7"]

    shipToOptions = api.stream("C", null, fields, *filter)?.withCloseable { it.collect {
        it.customerId + " - " + (it.name ?: "") + " - " + (it.attribute5 ?: "") + " - " + (it.attribute7 ?: "")
    }}
}

return shipToOptions