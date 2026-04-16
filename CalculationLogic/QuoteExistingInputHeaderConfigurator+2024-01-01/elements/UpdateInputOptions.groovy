def contractMap = (!api.isInputGenerationExecution() && out.FindContractNumbers) ? (out.FindContractNumbers as Map) : [:]
def contractDetailsMap = (!api.isInputGenerationExecution() && out.FindContractDetails) ? (out.FindContractDetails as Map) : [:]
def soldToNames = (!api.isInputGenerationExecution() && out.FindSoldToNames) ? (out.FindSoldToNames as Map) : [:]

def labels = new LinkedHashMap<>(contractMap.size())
contractMap.each { k, v ->
    def sapContract = (k ? k.substring(0, k.indexOf('|') >= 0 ? k.indexOf('|') : k.length()).trim() : "")
    def soldName = soldToNames?.get(v.SoldTo)
    def namePart = (soldName ? soldName : "") + (v.ShiptoName ? " - " + v.ShiptoName : "")
    labels[k] = namePart ? sapContract + " - " + namePart : sapContract
}

def input = InputContractNumber.input
def allKeysList = contractMap.keySet() as ArrayList

input.setValueOptions(["Select All", *allKeysList])
input.setConfigParameter("labels", labels)

def selectedValues = api.isInputGenerationExecution() ? null : input.getValue()
def soldToValues = api.isInputGenerationExecution() ? null : out.GetSoldToValues
def selectedShipTo = InputShipTo.input?.getValue()?.collect { it.split(" - ").getAt(0).trim() } as List
def selectedMaterial = out.GetMaterialValues
def selectedSalesPerson = InputSalesPerson.input?.getValue()

if(selectedValues?.any{ it  == "Select All" }) {
    input.setValue(allKeysList)
    selectedValues = allKeysList
}

if (selectedValues) {
    updateAllDropdowns(contractMap, selectedValues, contractDetailsMap, true)
} else if (selectedShipTo) {
    def target = selectedShipTo as Set
    def options = contractMap.findAll { k, v -> target.contains(v?.ShipTo) }.keySet().toList()
    input.setValueOptions(["Select All", *options])

    updateMaterialAndSalesPersonDropdown(options, contractDetailsMap)
} else if (soldToValues) {
    def stList = (soldToValues as Set).toList()
    def shipToOptions = findShipToOptions(stList)
    if (shipToOptions) {
        def validShipTos = (contractMap.values()*.ShipTo as Set)
        def opts = shipToOptions.findAll { k, _ -> validShipTos.contains(k) }.values().toList()
        InputShipTo.input.setValueOptions(["Select All", *opts])
    }

    def filteredContractKeys = contractMap.findAll { k, v -> stList.contains(v?.SoldTo) }.keySet().toList()
    updateMaterialAndSalesPersonDropdown(filteredContractKeys, contractDetailsMap)
} else if (selectedMaterial || selectedSalesPerson) {
    updateAllDropdowns(contractMap, contractMap.keySet().toList(), contractDetailsMap, false)
}

return null

Map findShipToOptions(List selectedSoldTos) {
    final tablesConstants = libs.QuoteConstantsLibrary.Tables

    def qapi = api.queryApi()
    def t1 = qapi.tables().customerExtensionRows(tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION)

    def extensionFilter = selectedSoldTos
            ? qapi.exprs().and(t1.customerId().in(selectedSoldTos), t1.PartnerFunction.in(["SH", "WE"]))
            : t1.customerId().isNotNull()

    def customerIds = qapi.source(t1, [t1.PartnerNumber], extensionFilter).stream { it.collect { it.PartnerNumber } } ?: []

    if (!customerIds) return [:]

    def t2 = qapi.tables().customers()
    def customerFilter = customerIds ? t2.customerId().in(customerIds) : t2.customerId().isNotNull()
    def fields = [t2.customerId(), t2.name(), t2.City, t2.Region]

    return qapi.source(t2, fields, customerFilter).stream {
        it.collectEntries {
            [(it.customerId): it.customerId + " - " + (it.name ?: "") + " - " + (it.City ?: "") + " - " + (it.Region ?: "") ]
        }
    } ?: [:]
}

def updateAllDropdowns(contractMap, contractKeys, contractDetailsMap, setValues) {
    final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

    def map = contractMap
    def selectedSoldTos = new LinkedHashSet()
    def selectedShipTos = new LinkedHashSet()
    def value
    contractKeys?.each {
        value = map?.get(it)
        if (value?.SoldTo) selectedSoldTos.add(value?.SoldTo)
        if (value?.ShipTo) selectedShipTos.add(value?.ShipTo)
    }

    def filterFormulaParam = [
            SelectedSoldTos: selectedSoldTos?.toList()
    ]

    InputSoldTo.input = libs.BdpLib.UserInputs.createInputCustomerGroup(
            headerConstants.SOLD_TO_ID,
            headerConstants.SOLD_TO_LABEL,
            false,
            false,
            headerConstants.SOLD_TO_URL,
            filterFormulaParam
    ).getFirstInput()

    if (setValues) InputSoldTo.input.setValue(selectedSoldTos?.toList())

    def shipToOptions = findShipToOptions(selectedSoldTos?.toList())
    def selectedShipToValues = shipToOptions?.findAll { k, v -> selectedShipTos.contains(k)}?.values() as List

    def options = shipToOptions ? ["Select All", *selectedShipToValues]?.unique() : []
    InputShipTo.input.setValueOptions(options)
    if (setValues) InputShipTo.input.setValue(selectedShipToValues)

    updateMaterialAndSalesPersonDropdown(contractKeys, contractDetailsMap)
}

def updateMaterialAndSalesPersonDropdown(contractKeys, contractDetailsMap) {
    final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

    def selectedMaterialValues = out.GetMaterialValues as List
    def selectedSalesPersonValue = InputSalesPerson.input?.getValue()
    def selectedLineRejected = InputLineRejected.input?.getValue()

    def allLines = []

    def bucket
    contractKeys.each { contractKey ->
        bucket = contractDetailsMap[contractKey]
        if (bucket?.Lines) {
            allLines.addAll(bucket.Lines)
        }
    }

    def rejBool, spList
    // --- Compute valid SalesPersons: filter lines by selected Material + LineRejected ---
    def linesForSalesPerson = allLines
    if (selectedMaterialValues) {
        linesForSalesPerson = linesForSalesPerson.findAll { selectedMaterialValues.contains(it.Material) }
    }

    if (selectedLineRejected) {
        rejBool = selectedLineRejected == "Y"
        linesForSalesPerson = linesForSalesPerson.findAll { it.RejectionFlag == rejBool }
    }
    def possibleSalesPersons = linesForSalesPerson.collect { it.SalesPerson }.findAll { it != null } as Set

    // --- Compute valid Materials: filter lines by selected SalesPerson + LineRejected ---
    def linesForMaterial = allLines
    if (selectedSalesPersonValue) {
        spList = selectedSalesPersonValue instanceof List ? selectedSalesPersonValue : [selectedSalesPersonValue]
        linesForMaterial = linesForMaterial.findAll { spList.contains(it.SalesPerson) }
    }
    if (selectedLineRejected) {
        rejBool = selectedLineRejected == "Y"
        linesForMaterial = linesForMaterial.findAll { it.RejectionFlag == rejBool }
    }
    def possibleMaterials = linesForMaterial.collect { it.Material }.findAll { it != null } as Set

    // --- Compute valid LineRejected values: filter lines by selected Material + SalesPerson ---
    def linesForRejected = allLines
    if (selectedMaterialValues) {
        linesForRejected = linesForRejected.findAll { selectedMaterialValues.contains(it.Material) }
    }
    if (selectedSalesPersonValue) {
        spList = selectedSalesPersonValue instanceof List ? selectedSalesPersonValue : [selectedSalesPersonValue]
        linesForRejected = linesForRejected.findAll { spList.contains(it.SalesPerson) }
    }
    def possibleRejectionFlags = linesForRejected.collect { it.RejectionFlag }.findAll { it != null } as Set

    // Update LineRejected options to only show valid values
    def allRejectedOptions = ["Y": "Y", "N": "N"]
    def validRejectedOptions = allRejectedOptions.findAll { k, v ->
        possibleRejectionFlags.contains(k == "Y")
    }
    if (validRejectedOptions) {
        InputLineRejected.input.setValueOptions(validRejectedOptions.keySet().toArray(new String[0]))
        InputLineRejected.input.setConfigParameter("labels", validRejectedOptions)
    }

    def filterFormulaParam = [
            MaterialList: possibleMaterials?.toList()
    ]

    InputMaterial.input = libs.BdpLib.UserInputs.createInputProductGroup(
            headerConstants.MATERIAL_ID,
            headerConstants.MATERIAL_LABEL,
            false,
            false,
            headerConstants.MATERIAL_URL,
            filterFormulaParam
    ).getFirstInput()

    def salesPersonOptions = out.FindSalesPersonOptions ? out.FindSalesPersonOptions as Map : [:]
    def finalSalesPersonOpt = salesPersonOptions.findAll { k, v -> possibleSalesPersons.contains(k) }

    InputSalesPerson.input.setValueOptions(finalSalesPersonOpt.keySet().collect { it.toString() }.toArray(new String[0]))
    InputSalesPerson.input.setConfigParameter("labels", finalSalesPersonOpt)
}