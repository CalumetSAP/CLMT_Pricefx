def filter = [
        Filter.equal("name", libs.DashboardConstantsLibrary.PricePublishing.CUSTOMER_EXTENSION_CUSTOMER_HIERARCHY),
        Filter.isNotEmpty("attribute7"),
        Filter.custom("attribute7 like '9%'")
]

def fields = ["customerId", "attribute2", "attribute4", "attribute7"]

List<String> selectedSoldTo = input?.get(libs.DashboardConstantsLibrary.PricePublishing.SOLD_TO_INPUT_KEY)

//def soldTos = out.FindSoldTo.collectEntries {[(it.customerId): it.attribute7]}

if(selectedSoldTo) {
    filter.add(Filter.in("customerId", selectedSoldTo))
}

def customerIds = api.stream("CX20", "attribute7", fields, true, *filter)?.withCloseable { it.collect() }

return customerIds