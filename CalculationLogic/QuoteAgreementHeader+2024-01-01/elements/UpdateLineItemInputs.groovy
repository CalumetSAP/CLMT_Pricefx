if (quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final dateUtils = libs.QuoteLibrary.DateUtils
final calculations = libs.QuoteLibrary.Calculations

// Option Dropdowns
def shipToOptions = out.FindCustomerShipTo
def salesPersonOptions = out.FindSalesPerson
def incoTermOptions = out.FindIncoTerm && !api.isInputGenerationExecution() ? out.FindIncoTerm as List : []
def freightTermOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["FreightTerm"] as Map : [:]
def currencyOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["Currency"] as Map : [:]
def numberOfDecimalsOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["NumberOfDecimals"] as Map : [:]
def priceTypeOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceType"] as Map : [:]
def pricingFormulasOptions = out.FindPricingFormulas && !api.isInputGenerationExecution() ? out.FindPricingFormulas as List : []
def meansOfTransportationOptions = out.FindMeansOfTransportation && !api.isInputGenerationExecution() ? out.FindMeansOfTransportation as List : []
def modeOfTransportationOptions = out.FindModeOfTransportation && !api.isInputGenerationExecution() ? out.FindModeOfTransportation as List : []
def pricelistOptions = out.FindPricelist && !api.isInputGenerationExecution() ? out.FindPricelist as Map : [:]
def plantShippingPointOptions = out.FindPlantShippingPoint && !api.isInputGenerationExecution() ? out.FindPlantShippingPoint as Map : [:]
def shippingPointNames = out.FindShippingPoint && !api.isInputGenerationExecution() ? out.FindShippingPoint as Map : [:]
def uomOptions = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions as Map : [:]
// String[] x = options.keySet().collect { it.toString() }.toArray(new String[0])
// Default dates
def defaultValidFromDate = dateUtils.getToday()
def days = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceValidTo"]?.values()?.find() as Integer : null
def defaultValidToDate = dateUtils.sumDays(defaultValidFromDate, days)

def plantOptions, shippingPointOptions

def creationWorkflowCurrentStep = quoteProcessor.getQuoteView()?.creationWorkflowCurrentStep
def creationWorkflowStepLabel = quoteProcessor.getQuoteView()?.creationWorkflowStepLabel
def commandName = api.currentContext().commandName
def user = api.local.loginName

def isFreightStep = calculations.isFreightGroupStep(commandName, creationWorkflowCurrentStep, creationWorkflowStepLabel, user)
def isFreightGroup = api.isUserInGroup(libs.QuoteConstantsLibrary.General.CREATION_WORKFLOW_STEP_2_GROUP, user)

//def priceProtectionConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
//        headerConstants.PRICE_PROTECTION_CONFIGURATOR_NAME
//)?.value ?: [:]

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String

    plantOptions = out.FindPlants && !api.isInputGenerationExecution() ? out.FindPlants?.getOrDefault(lnProduct.sku, []) as List : []

    def plant = calculations.getInputValue(lnProduct, lineItemConstants.PLANT_ID)
    if (plant) {
        shippingPointOptions = plantShippingPointOptions?.get(plant.split(" ")?.getAt(0))?.collect { shippingPointNames?.get(it) }
    } else {
        shippingPointOptions = shippingPointNames?.values()?.toList()
    }

    def uoms = ["Flat Rate"]
    def uomsPerMaterial = uomOptions?.getOrDefault(lnProduct.sku, [])
    uomsPerMaterial?.add("KG")
    uomsPerMaterial?.sort()
    uomsPerMaterial?.unique()
    uoms.addAll(uomsPerMaterial)

    // Update ReadOnlyOptions depending on CF
    def freightEstimateValue = calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_ESTIMATE_ID)
    updateReadOnlyOptions(lineId, lnProduct.inputs, freightEstimateValue, isFreightStep, isFreightGroup)

    // Get Previous Values
    def previousShipTo = calculations.getInputValue(lnProduct, lineItemConstants.SHIP_TO_ID)
    def previousPlant = calculations.getInputValue(lnProduct, lineItemConstants.PLANT_ID)
    def previousIncoTerm = calculations.getInputValue(lnProduct, lineItemConstants.INCO_TERM_ID)
    def previousFreightTerm = calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_TERM_ID)
    def previousShippingPoint = calculations.getInputValue(lnProduct, lineItemConstants.SHIPPING_POINT_ID)
    def previousCurrency = calculations.getInputValue(lnProduct, lineItemConstants.CURRENCY_ID)
    def previousNumberOfDecimal = calculations.getInputValue(lnProduct, lineItemConstants.NUMBER_OF_DECIMALS_ID)
    def previousSalesPerson = calculations.getInputValue(lnProduct, lineItemConstants.SALES_PERSON_ID)
    def previousMeanOfTransportation = calculations.getInputValue(lnProduct, lineItemConstants.MEANS_OF_TRANSPORTATION_ID)
    def previousModeOfTransportation = calculations.getInputValue(lnProduct, lineItemConstants.MODE_OF_TRANSPORTATION_ID)
    def previousPricelist = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_LIST_ID)
    def previousPriceType = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
    def previousFreightUOM = calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_UOM_ID)
    def previousSalesUOM = calculations.getInputValue(lnProduct, lineItemConstants.SALES_UOM_ID)
    def previousPricingUOM = calculations.getInputValue(lnProduct, lineItemConstants.PRICING_UOM_ID)

    // Update Option Inputs
    updateOptionInput(lineId, lineItemConstants.SHIP_TO_ID, previousShipTo, shipToOptions)
    updateOptionInput(lineId, lineItemConstants.PLANT_ID, previousPlant, plantOptions?.sort())
    updateOptionInput(lineId, lineItemConstants.INCO_TERM_ID, previousIncoTerm, incoTermOptions)
    updateOptionInputWithMap(lineId, lineItemConstants.FREIGHT_TERM_ID, previousFreightTerm, freightTermOptions)
    updateOptionInput(lineId, lineItemConstants.SHIPPING_POINT_ID, previousShippingPoint, shippingPointOptions?.findAll()?.sort())
    updateOptionInputWithMap(lineId, lineItemConstants.CURRENCY_ID, previousCurrency, currencyOptions)
    updateOptionInputWithMap(lineId, lineItemConstants.NUMBER_OF_DECIMALS_ID, previousNumberOfDecimal, numberOfDecimalsOptions)
//    updateOptionInput(lineId, lineItemConstants.PRICING_FORMULA_ID, pricingFormulasOptions)
    updateOptionInput(lineId, lineItemConstants.SALES_PERSON_ID, previousSalesPerson, salesPersonOptions)
    updateOptionInput(lineId, lineItemConstants.MEANS_OF_TRANSPORTATION_ID, previousMeanOfTransportation, meansOfTransportationOptions)
    updateOptionInput(lineId, lineItemConstants.MODE_OF_TRANSPORTATION_ID, previousModeOfTransportation, modeOfTransportationOptions)
    updateOptionInputWithMap(lineId, lineItemConstants.PRICE_LIST_ID, previousPricelist, pricelistOptions)
    updateOptionInputWithMap(lineId, lineItemConstants.PRICE_TYPE_ID, previousPriceType, priceTypeOptions)
    updateOptionInput(lineId, lineItemConstants.FREIGHT_UOM_ID, previousFreightUOM, uoms)
    updateOptionInput(lineId, lineItemConstants.SALES_UOM_ID, previousSalesUOM, uomsPerMaterial)
    updateOptionInput(lineId, lineItemConstants.PRICING_UOM_ID, previousPricingUOM, uomsPerMaterial)

    // Update Default values
    def previousPer = calculations.getInputValue(lnProduct, lineItemConstants.PER_ID)
    updateValue(lineId, lineItemConstants.PER_ID, BigDecimal.ONE, previousPer)

    def previousValidFromDate = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_VALID_FROM_ID)
    updateValue(lineId, lineItemConstants.PRICE_VALID_FROM_ID, defaultValidFromDate, previousValidFromDate)

    def previousValidToDate = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_VALID_TO_ID)
    updateValue(lineId, lineItemConstants.PRICE_VALID_TO_ID, defaultValidToDate, previousValidToDate)

//    updateReadOnly(lineId, lineItemConstants.FREIGHT_AMOUNT_ID, !freightEstimateValue)
//    updateReadOnly(lineId, lineItemConstants.FREIGHT_VALID_TO_ID, !freightEstimateValue)

    def previousValidToFreightDate = calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_VALID_TO_ID)
    updateValue(lineId, lineItemConstants.FREIGHT_VALID_TO_ID, defaultValidToDate, previousValidToFreightDate)

    def previousValidFromFreightDate = calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_VALID_FROM_ID)
    updateValue(lineId, lineItemConstants.FREIGHT_VALID_FROM_ID, defaultValidFromDate, previousValidFromFreightDate)

    updateValue(lineId, lineItemConstants.NUMBER_OF_DECIMALS_ID, "4", previousNumberOfDecimal)
//    if (priceProtectionConfigurator?.get(headerConstants.PRICE_PROTECTION_ID) != null) updateValue(lineId, lineItemConstants.PRICE_TYPE_ID, "2", previousPriceType)
}

return null

def updateOptionInput(String lineId, name, previousValue, options) {
    def config = [:]
    if (previousValue == null && options?.size() == 1) config["value"] = options instanceof List ? options.find() : options.keySet().find()
    config["name"] = name
    config["valueOptions"] = options
    quoteProcessor.addOrUpdateInput(lineId, config)
}

def updateOptionInputWithMap(String lineId, name, previousValue, Map options) {
    String[] valueOptions = options.keySet().collect { it.toString() }.toArray(new String[0])
    def config = [:]
    if (previousValue == null && options?.size() == 1) config["value"] = options.keySet().find()
    config["name"] = name
    config["labels"] = options
    config["valueOptions"] = valueOptions
    quoteProcessor.addOrUpdateInput(lineId, config)
}

def updateValue(String lineId, name, defaultValue, previousValue) {
    if (previousValue != null) return
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name" : name,
            "value": defaultValue,
    ])
}

def updateReadOnly(String lineId, name, readOnly) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"    : name,
            "readOnly": readOnly,
    ])
}

def updateReadOnlyOptions(String lineId, inputs, freightEstimateValue, isFreightStep, isFreightGroup) {
    def lineItemConstants = libs.QuoteConstantsLibrary.LineItem

    inputs?.each {
        quoteProcessor.addOrUpdateInput(
                lineId, [
                "name"    : it.name,
                "readOnly": isFreightStep,
        ])
    }

    if (isFreightStep && freightEstimateValue && isFreightGroup) {
        def freightInputs = [
                lineItemConstants.FREIGHT_ESTIMATE_ID,
                lineItemConstants.FREIGHT_AMOUNT_ID,
                lineItemConstants.FREIGHT_VALID_TO_ID,
                lineItemConstants.FREIGHT_VALID_FROM_ID,
        ]
        freightInputs?.each {
            quoteProcessor.addOrUpdateInput(
                    lineId, [
                    "name"    : it,
                    "readOnly": false,
            ])
        }
    } else {
        api.local.readyOnlyFields?.each {
            quoteProcessor.addOrUpdateInput(
                    lineId, [
                    "name"    : it,
                    "readOnly": true,
            ])
        }
    }
}