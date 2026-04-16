if (api.isInputGenerationExecution() || !(!out.InputSoldTo?.getFirstInput()?.getValue() && out.InputShipTo?.getFirstInput()?.getValue())) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

// Find Sold to
def selectedShipTo = out.InputShipTo?.getFirstInput()?.getValue()
def customerFilter = [
        Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
        Filter.equal("attribute1", selectedShipTo),
        Filter.in("attribute4", ["SH", "WE"])
]
def fields = ["customerId"]

def soldTo = api.find("CX", 0, 1, null, fields, *customerFilter)?.find()?.customerId

out.InputSoldTo?.getFirstInput()?.setValue(soldTo)

// Find Division
customerFilter = [
        Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
        Filter.equal("customerId", soldTo),
        Filter.in("attribute4", ["SP", "AG"])
]
fields = ["attribute3"]

def selectedDivision = api.stream("CX", null, fields, *customerFilter)
        ?.withCloseable { it.collect { it.attribute3 } }
        ?.unique() ?: []
def allDivisions = api.local.division ? api.local.division as Map : [:]
def options = allDivisions?.findAll { key, value -> selectedDivision.contains(key) }

String[] x = options.keySet().collect { it.toString() }.toArray(new String[0])

out.InputDivision?.getFirstInput()?.setValueOptions(x)
out.InputDivision?.getFirstInput()?.setConfigParameter("labels", options)
out.InputDivision?.getFirstInput()?.setReadOnly(false)

if (options.size() == 1) {
    def division = options?.keySet()?.find()
    out.InputDivision?.getFirstInput()?.setValue(division)

    // Find Sales Org
    customerFilter = [
            Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
            Filter.equal("customerId", soldTo),
            Filter.equal("attribute3", division),
            Filter.in("attribute4", ["SP", "AG"])
    ]
    fields = ["attribute6"]

    def selectedSalesOrg = api.stream("CX", null, fields, *customerFilter)
            ?.withCloseable { it.collect { it.attribute6 } }
            ?.unique() ?: []
    def allSalesOrg = api.local.salesOrg ? api.local.salesOrg as Map : [:]
    options = allSalesOrg?.findAll { key, value -> selectedSalesOrg.contains(key) }

    x = options.keySet().collect { it.toString() }.toArray(new String[0])

    out.InputSalesOrg?.getFirstInput()?.setValueOptions(x)
    out.InputSalesOrg?.getFirstInput()?.setConfigParameter("labels", options)
    out.InputSalesOrg?.getFirstInput()?.setReadOnly(false)

    if (options.size() == 1) {
        out.InputSalesOrg?.getFirstInput()?.setValue(options?.keySet()?.find())
    }
}


