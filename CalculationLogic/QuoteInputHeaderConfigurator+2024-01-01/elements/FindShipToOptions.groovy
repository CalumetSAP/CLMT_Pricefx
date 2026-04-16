if (api.isInputGenerationExecution() || out.InputSoldToOnlyQuote?.getFirstInput()?.getValue()) return []

def selectedSalesOrg = out.InputSalesOrg?.getFirstInput()?.getValue()

if (!selectedSalesOrg) return []

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def selectedSoldTo = out.InputSoldTo?.getFirstInput()?.getValue()
def selectedDivision = out.InputDivision?.getFirstInput()?.getValue()
def customerFilter = [
        Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
        Filter.equal("customerId", selectedSoldTo),
        Filter.equal("attribute3", selectedDivision),
        Filter.equal("attribute6", selectedSalesOrg),
        Filter.in("attribute4", ["SH", "WE"])
]
def fields = ["attribute1"]

def customerIds = api.stream("CX", null, fields, *customerFilter)?.withCloseable { it.collect { it.attribute1 } }?.unique()

customerFilter = [
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

return api.stream("C", null, fields, *customerFilter)?.withCloseable { it.collect {
    it.customerId + " - " + (it.name ?: "") + " - " + (it.attribute5 ?: "") + " - " + (it.attribute7 ?: "")
}}