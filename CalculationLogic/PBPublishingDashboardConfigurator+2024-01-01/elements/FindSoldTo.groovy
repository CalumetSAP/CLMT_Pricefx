String masterParent = input?.get(libs.DashboardConstantsLibrary.PricePublishing.MASTER_PARENT_INPUT_KEY)
List<String> salesOrg = input?.get(libs.DashboardConstantsLibrary.PricePublishing.SALES_ORG_INPUT_KEY)
String division = input?.get(libs.DashboardConstantsLibrary.PricePublishing.DIVISION_INPUT_KEY)

def filter = [
        Filter.equal("name", libs.DashboardConstantsLibrary.PricePublishing.CUSTOMER_EXTENSION_CUSTOMER_HIERARCHY),
        Filter.equal("attribute7", masterParent),
        Filter.isNotEmpty("attribute7"),
]

def fields = ["customerId","attribute7"]

def customerIds = api.stream("CX20", "customerId", fields, true, *filter)?.withCloseable { it.collect{ it.customerId } }

def filterPartner = [
        Filter.equal("name", libs.DashboardConstantsLibrary.PricePublishing.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
        Filter.equal("attribute4", "SP")
]

if (salesOrg) filterPartner.add(Filter.in("attribute6", salesOrg))
if (division) filterPartner.add(Filter.equal("attribute3", division))
if (customerIds) filterPartner.add(Filter.in("customerId", customerIds))

List<String> selectedShipTo = input?.get(libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_INPUT_KEY)

def shipTos = out.FindShipTo.collectEntries {[(it.attribute1): it.customerId]}

if(selectedShipTo) {
    filterPartner.add(Filter.in("attribute1", selectedShipTo.collect{shipTos?.get(it)}))
}

def fieldsPartner = ["customerId", "attribute1"]

def partners = api.stream("CX20", "customerId", fieldsPartner, true, *filterPartner)?.withCloseable { it.collect()}

return partners