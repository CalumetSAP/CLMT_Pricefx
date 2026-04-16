if (api.isInputGenerationExecution()) return

setPostWorkflow("PostApprovalQuote")

if (!api.local.approvalRequired) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs
final format = libs.BdpLib.Format
final roundingUtils = libs.QuoteLibrary.RoundingUtils

def hasFormula = api.local.hasPricingFormula && api.local.formulaApprovers
def hasFirstLevel = api.local.approversMap?.get("1")?.size() > 0
def hasSecondLevel = api.local.approversMap?.get("2")?.size() > 0
def hasThirdLevel = api.local.approversMap?.get("3")?.size() > 0

def approvers

if (hasFirstLevel) {
    approvers = api.local.approversMap?.get("1")?.unique()
    workflow.addApprovalStep("1st Level")
            .withMinApprovalsNeeded(approvers?.size())
            .withApprovers(*approvers)
            .withReasons("Discount")
}

if (hasFormula || hasSecondLevel) {
    def formulaApprovers = api.local.formulaApprovers ?: []
    approvers = api.local.approversMap?.get("2") ?: []

    def approverReasonMap = [:]

    formulaApprovers.each { approver ->
        approverReasonMap[approver] = "Formula"
    }

    approvers.each { approver ->
        if (formulaApprovers.contains(approver)) {
            approverReasonMap[approver] = "Discount & Formula"
        } else {
            approverReasonMap[approver] = "Discount"
        }
    }

    workflow.addApprovalStep("2nd Level")
            .withMinApprovalsNeeded(approverReasonMap?.size())
            .withApprovers(*(approverReasonMap.keySet() as List))
            .withReasons(*(approverReasonMap.values() as List))
}

if (hasThirdLevel) {
    approvers = api.local.approversMap?.get("3")?.unique()
    workflow.addApprovalStep("3rd Level")
            .withMinApprovalsNeeded(approvers?.size())
            .withApprovers(*approvers)
            .withReasons("Discount")
}

def dropdownOptions = getDropdownOptionsValues()
def referencePeriodMap = dropdownOptions?.ReferencePeriod

def uomConversionTable = findUOMConversionTable(quote.lineItems)
def globalUOMConversionTable = libs.QuoteLibrary.Conversion.getGlobalUOMConversion()

def optionalColumns = [
        IndexNumber1        : "Index Number 1",
        IndexNumber2        : "Index Number 2",
        IndexNumber3        : "Index Number 3",
        ReferencePeriod     : "Reference Period",
        Adder               : "Adder",
        RecalculationDate   : "Recalculation Date",
        RecalculationPeriod : "Recalculation Period",
        PriceInUG6          : "Price in \$/UG6",
        FreightAmount       : "Freight Amount",
        DeliveredPrice      : "Delivered Price"
]

def columnNames = ["Approver", "Contract Line", "Material", "Ship-to", "Plant", "Sales Person", "Recommended Price", "Previous Price",
                   "Price", "Change From Previous Price", "Material Margin", "Material Margin %", "Index Number 1",
                   "Index Number 2", "Index Number 3", "Reference Period", "Adder", "Recalculation Date",
                   "Recalculation Period", "Cost", "Price in \$/UG6", "Freight Amount", "Delivered Price"]

def lineItems = []
def approver, discountApprover, formulaApprover, row, configurator, numberOfDecimals, recommendedPrice,
        dsValues, previousPrice, previousPriceUOM,  price, cost, materialMargin, materialMarginPercent,
        indexNumbers, adderValue, referencePeriodId, freightAmount, deliveredPrice, uom, conversionFactor, convertedPrice
quote.lineItems?.findAll { !it.inputs.find { it.name == lineItemConstants.REJECTION_REASON_ID }?.value }?.each { item ->
    discountApprover = findOutput(item, lineItemOutputsConstants.APPROVER_ID)
    formulaApprover = findOutput(item, lineItemOutputsConstants.FORMULA_APPROVER_ID)
    if (discountApprover && formulaApprover && discountApprover != formulaApprover) {
        approver = "${discountApprover}, ${formulaApprover}"
    } else if (discountApprover) {
        approver = discountApprover
    } else if (formulaApprover) {
        approver = formulaApprover
    } else {
        approver = null
    }

    //Ignore items that don't have approvers
    if (approver) {
        row = [:]

        configurator = item.inputs.find { it.name == lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME }?.value

        numberOfDecimals = (findInput(configurator, lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: "2").toInteger()
        uom = findInput(configurator, lineItemConstants.PRICING_UOM_ID)

        row.Approver                = approver
        row.ContractLine            = findOutput(item, "SAPLineId")
        row.Material                = "${item.sku} - ${item.label}"
        row.ShipTo                  = "${findInput(configurator, lineItemConstants.SHIP_TO_ID)}, " +
                "${findInput(configurator, lineItemConstants.SHIP_TO_INDUSTRY_ID)}, " +
                "${findInput(configurator, lineItemConstants.SHIP_TO_ADDRESS_ID)}, " +
                "${findInput(configurator, lineItemConstants.SHIP_TO_CITY_ID)}, " +
                "${findInput(configurator, lineItemConstants.SHIP_TO_STATE_ID)}, " +
                "${findInput(configurator, lineItemConstants.SHIP_TO_COUNTRY_ID)}"
        row.Plant                   = findInput(configurator, lineItemConstants.PLANT_ID)?:findOutput(item, lineItemOutputsConstants.PLANT_ID)
        row.SalesPerson             = findInput(configurator, lineItemConstants.SALES_PERSON_ID)

        recommendedPrice = (findOutput(item, lineItemOutputsConstants.RECOMMENDED_PRICE_ID) ?: BigDecimal.ZERO).toBigDecimal()
        row.RecommendedPrice        = roundingUtils.round(recommendedPrice, numberOfDecimals)?.toString() ?: ""

        dsValues = item.inputs.find {it.name == lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID }?.value
        previousPrice = dsValues?.Price
        previousPriceUOM = dsValues?.PricingUOM
        row.PreviousPrice = "${roundingUtils.round(previousPrice, numberOfDecimals)?.toString() ?: ""} ${previousPriceUOM}"
        price = (findInput(configurator, lineItemConstants.PRICE_ID) ?: BigDecimal.ZERO).toBigDecimal()
        cost = (findOutput(item, lineItemOutputsConstants.COST_ID) ?: BigDecimal.ZERO).toBigDecimal()
        row.Price                   = "${roundingUtils.round(price, numberOfDecimals)?.toString() ?: ""} ${uom}"
        row.ChangeFromPreviousPrice = price && previousPrice ? roundingUtils.round(price - previousPrice, numberOfDecimals)?.toString() : ""
        materialMargin = price && cost ? price - cost : null
        row.MaterialMargin          = roundingUtils.round(materialMargin, numberOfDecimals)?.toString() ?: ""
        materialMarginPercent = materialMargin != null ? materialMargin / price : null
        row.MaterialMarginPercent   = format.percentAfter(materialMarginPercent) ?: ""

        indexNumbers = findInput(configurator, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)
        referencePeriodId = findInput(configurator, lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)?.toString()
        row.IndexNumber1        = indexNumbers ? indexNumbers[0] ?: "" : ""
        row.IndexNumber2        = indexNumbers ? indexNumbers[1] ?: "" : ""
        row.IndexNumber3        = indexNumbers ? indexNumbers[2] ?: "" : ""
        row.ReferencePeriod     = referencePeriodMap?.get(referencePeriodId)
        adderValue = (findInput(configurator, lineItemConstants.PF_CONFIGURATOR_ADDER_ID) ?: BigDecimal.ZERO).toBigDecimal()
        row.Adder               = indexNumbers ? "${roundingUtils.round(adderValue, numberOfDecimals)?.toString() ?: ""} ${findInput(configurator, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID)}" : ""
        row.RecalculationDate   = findInput(configurator, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID)?.toString()
        row.RecalculationPeriod = findInput(configurator, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID)?.toString()

        row.Cost = roundingUtils.round(cost, numberOfDecimals)?.toString() ?: ""

        if (["LB", "KG"].contains(uom)) {
            conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(item.sku, uom, "UG6", uomConversionTable, globalUOMConversionTable)?.toBigDecimal()
            if (conversionFactor) {
                convertedPrice = conversionFactor * roundingUtils.round(price, numberOfDecimals)
                row.PriceInUG6 = roundingUtils.round(convertedPrice, numberOfDecimals)?.toString() ?: ""
            }
        } else {
            row.PriceInUG6 = ""
        }

        if (findInput(configurator, lineItemConstants.FREIGHT_TERM_ID) == "3") {
            freightAmount = (findInput(configurator, lineItemConstants.FREIGHT_AMOUNT_ID) ?: BigDecimal.ZERO).toBigDecimal()
            row.FreightAmount = roundingUtils.round(freightAmount, numberOfDecimals)?.toString() ?: ""

            deliveredPrice = (findInput(configurator, lineItemConstants.DELIVERED_PRICE_ID)?: BigDecimal.ZERO).toBigDecimal()
            row.DeliveredPrice = roundingUtils.round(deliveredPrice, numberOfDecimals)?.toString() ?: ""
        } else {
            row.FreightAmount = ""
            row.DeliveredPrice = ""
        }

        lineItems << row
    }
}

//Remove optional columns that are empty
optionalColumns.each { key, columnName ->
    if (!lineItems[key].any { it }) {
        lineItems.each { it.remove(key) }
        columnNames.remove(columnName)
    }
}

//Sort lineItems by Approvers
lineItems.sort { a, b ->
    a.Approver <=> b.Approver ?: a.PH1 <=> b.PH1
}
lineItems.each { it.remove("PH1") }

def index = 0
//Transform to list to show dynamically in the template
lineItems = lineItems.collect { [
        oddEvenClass: (index++ % 2 == 0) ? 'even' : 'odd',
        lineItem    : it.collect { key, value -> [columnName: key+"Row", columnValue: value]}
]}

def soldToValues = quote?.inputs?.find { it.name == "InputsConfigurator"}?.value?.SoldToInput

def customerFields = ["customerId", "name"]
def customerFilter = Filter.in("customerId", soldToValues)

def selectedCustomer = api.find("C", 0, 1, null, customerFields, customerFilter)?.find()

def SoldTo = selectedCustomer?.name ?: selectedCustomer?.customerId

def headerDescription = quote?.headerText

def map = [
        "soldTo"           : SoldTo,
        "headerDescription": headerDescription,
        "columnNames"      : columnNames,
        "items"            : lineItems,
]
workflow.withDataMap(map)

def setPostWorkflow(String postApprovalStepLogicUniqueName) {
    workflow.withDefaultPostApprovalStepLogic(postApprovalStepLogicUniqueName)
            .withRunDefaultPostApprovalStepLogicOnEmptyWorkflow(true)
}

def findInput (configurator, name) {
    return configurator?.get(name) ?: ""
}

def findOutput (item, resultName) {
    return item.outputs.find { it.resultName == resultName }?.result ?: ""
}

def getDropdownOptionsValues() {
    def tablesConstants = libs.QuoteConstantsLibrary.Tables

    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.in("key2", ["ReferencePeriod"]),
    ]
    def data = api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)

    data.inject([:]) { formatted, entry ->
        String key = entry["key2"]
        def value = [(entry["key3"]) : (entry["attribute1"])]
        formatted[key] = formatted.containsKey(key) ? formatted[key] + value : value
        formatted
    } ?: [:]
}

def findUOMConversionTable(List lineItems) {
    def skus = lineItems?.collect { it.sku }
    if (!skus) return [:]

    def fields = ["sku", "unitOfMeasure"]
    def filter = Filter.in("sku", skus)

    def productMasterData = api.stream("P", null, fields, filter)?.withCloseable {
        it.collectEntries {
            [(it.sku): [
                    UOM: it.unitOfMeasure,
            ]]
        }
    }

    return libs.QuoteLibrary.Conversion.getUOMConversion(skus, productMasterData)
}