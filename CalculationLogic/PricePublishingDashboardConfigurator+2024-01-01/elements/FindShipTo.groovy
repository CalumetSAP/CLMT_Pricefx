List<String> salesOrg = input.get(libs.DashboardConstantsLibrary.PricePublishing.SALES_ORG_INPUT_KEY)
String division = input.get(libs.DashboardConstantsLibrary.PricePublishing.DIVISION_INPUT_KEY)
List<String> soldTo = input?.get(libs.DashboardConstantsLibrary.PricePublishing.SOLD_TO_INPUT_KEY)

// Fields
def filterPartner = [
        Filter.equal("name", libs.DashboardConstantsLibrary.PricePublishing.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
        Filter.equal("attribute4", "SH")
]

// Prefilter by input selection
if (salesOrg) filterPartner.add(Filter.in("attribute6", salesOrg))
if (division) filterPartner.add(Filter.equal("attribute3", division))
if (soldTo) filterPartner.add(Filter.in("customerId", soldTo))

def fieldsPartner = ["customerId", "attribute1"]

def partners = api.stream("CX20", "customerId", fieldsPartner, true, *filterPartner)?.withCloseable { it.collect()}

return partners