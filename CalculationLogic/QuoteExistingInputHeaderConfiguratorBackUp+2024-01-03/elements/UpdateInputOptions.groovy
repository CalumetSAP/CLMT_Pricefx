final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def contractMap = out.FindContractNumbers && !api.isInputGenerationExecution() ? out.FindContractNumbers as Map : [:]

def labels = contractMap?.collectEntries { key, value ->
    def sapContract = key ? key.split("\\|").getAt(0).trim() : ""
    def labelKey = key ?: ""
    def namePart = value.SoldtoName || value.ShiptoName ? value.SoldtoName + " - " + value.ShiptoName : ""
    def label = namePart ? sapContract + " - " + namePart : sapContract
    [(labelKey): label]
}

def input = InputContractNumber.input

input.setValueOptions(["Select All", *contractMap?.keySet()?.toList()])
input.setConfigParameter("labels", labels)

def selectedValues = api.isInputGenerationExecution() ? null : input.getValue()
def soldToValues = api.isInputGenerationExecution() ? null : out.GetSoldToValues
def selectedShipTo = InputShipTo.input?.getValue()?.collect { it.split(" - ").getAt(0).trim() } as List

if(selectedValues?.any{ it  == "Select All" }) {
    input.setValue(contractMap?.keySet()?.toList())
    selectedValues = contractMap?.keySet()?.toList()
}

if (selectedValues) {
    def map = out.FindContractNumbers as Map
    def selectedSoldTos = []
    def selectedShipTos = []
    def value
    selectedValues?.each {
        value = map?.get(it)
        selectedSoldTos.add(value?.SoldTo)
        selectedShipTos.add(value?.ShipTo)
    }

    selectedSoldTos = selectedSoldTos?.unique()

    def filterFormulaParam = [
            SelectedSoldTos: selectedSoldTos
    ]

    InputSoldTo.input = libs.BdpLib.UserInputs.createInputCustomerGroup(
            headerConstants.SOLD_TO_ID,
            headerConstants.SOLD_TO_LABEL,
            false,
            false,
            headerConstants.SOLD_TO_URL,
            filterFormulaParam
    ).getFirstInput()

    InputSoldTo.input.setValue(selectedSoldTos)

    def findShipToOptions = findShipToOptions(selectedSoldTos)
    def selectedShipToValues = findShipToOptions?.findAll { k, v -> selectedShipTos.contains(k)}?.values() as List

    def options = findShipToOptions ? ["Select All", *selectedShipToValues]?.unique() : []
    InputShipTo.input.setValueOptions(options)
    InputShipTo.input.setValue(selectedShipToValues)
} else if (selectedShipTo) {
    def options = out.FindContractNumbers?.findAll { key, value -> selectedShipTo?.any { value?.ShipTo?.contains(it) }}?.keySet()?.toList()
    input.setValueOptions(["Select All", *options])
} else if (soldToValues) {
    def map = out.FindContractNumbers as Map
    def findShipToOptions = findShipToOptions(soldToValues)
    def shipToList = findShipToOptions?.keySet()?.toList()

    def filteredShipToList = shipToList.findAll { shipTo ->
        map.values().any { entry ->
            entry.ShipTo == shipTo
        }
    }

    def options = findShipToOptions?.findAll { k, v -> filteredShipToList.contains(k)}?.values()?.toList()

    InputShipTo.input.setValueOptions(["Select All", *options])
}

return null

def getShipToValues(selectedSoldTo, shipToList) {
    def shipToOptions = out.FindShipToOptions ? out.FindShipToOptions as Map : findShipToOptions([selectedSoldTo])
    def shipToValues = []
    shipToList?.each{ shipToValues.add(shipToOptions.get(it)) }

    shipToValues = shipToValues?.findAll { it }
    return shipToValues
}

def findShipToOptions(List selectedSoldTos) {
    final tablesConstants = libs.QuoteConstantsLibrary.Tables

    def filter = [
            Filter.equal("name", tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION),
            Filter.in("attribute4", ["SH", "WE"]),
            Filter.in("customerId", selectedSoldTos)]
    def fields = ["attribute1"]

    def customerIds = api.stream("CX", null, fields, *filter)?.withCloseable { it.collect { it.attribute1 } }?.unique()

    filter = [Filter.in("customerId", customerIds)]
    fields = ["customerId", "name", "attribute5", "attribute7"]

    return api.stream("C", null, fields, *filter)?.withCloseable { it.collectEntries {
        [(it.customerId): it.customerId + " - " + (it.name ?: "") + " - " + (it.attribute5 ?: "") + " - " + (it.attribute7 ?: "")]
    }}

}