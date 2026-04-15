import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final dateUtils = libs.QuoteLibrary.DateUtils
//final calculations = libs.QuoteLibrary.Calculations

def shipTos = out.FindCustomerShipTo && !api.isInputGenerationExecution() ? out.FindCustomerShipTo as List : []
def priceTypeOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceType"] as Map : [:]
def indexNumberOptions = out.FindIndexValues && !api.isInputGenerationExecution() ? out.FindIndexValues as List : []
def referencePeriodOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["ReferencePeriod"] as Map : [:]
def recalculationPeriodOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["RecalculationPeriod"] as Map : [:]
//def priceProtectionOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceProtection"] as Map : [:]
def movementTimingOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["MovementTiming"] as Map : [:]
def pricelistOptions = out.FindPricelist && !api.isInputGenerationExecution() ? out.FindPricelist as Map : [:]
def shippingPointNames = out.FindShippingPoint && !api.isInputGenerationExecution() ? out.FindShippingPoint as Map : [:]
def uomOptions = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions as Map : [:]
def currencyOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["Currency"] as Map : [:]
def numberOfDecimalsOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["NumberOfDecimals"] as Map : [:]
//def productMasterData = out.FindProductMasterData ?: [:]
//def exclusionsMap = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindExclusions : null

def defaultValidFromDate = dateUtils.getToday()
def days = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceValidTo"]?.values()?.find() as Integer : null
def defaultValidToDate = dateUtils.sumDays(defaultValidFromDate, days)

//def priceProtectionConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
//        headerConstants.PRICE_PROTECTION_CONFIGURATOR_NAME
//)?.value ?: [:]
//
//def ppValues = [
//        PriceProtection: priceProtectionConfigurator?.get(headerConstants.PRICE_PROTECTION_ID),
//        NumberOfDays   : priceProtectionConfigurator?.get(headerConstants.NUMBER_OF_DAYS_ID),
//        MovementTiming : priceProtectionConfigurator?.get(headerConstants.MOVEMENT_TIMING_ID),
//        MovementStart  : priceProtectionConfigurator?.get(headerConstants.MOVEMENT_START_ID),
//        MovementDay    : priceProtectionConfigurator?.get(headerConstants.MOVEMENT_DAY_ID),
//]

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedSoldTo = customerConfigurator?.get(headerConstants.SOLD_TO_ID)

def params = [
        ShipTo             : shipTos,
        PriceTypes         : priceTypeOptions,
        IndexNumber        : indexNumberOptions,
        ReferencePeriod    : referencePeriodOptions,
        RecalculationPeriod: recalculationPeriodOptions,
//        PPDefaultValues    : ppValues,
//        PriceProtection    : priceProtectionOptions,
        MovementTiming     : movementTimingOptions,
        Pricelists         : pricelistOptions,
        ShippingPoints     : shippingPointNames,
        Currency           : currencyOptions,
        DecimalPlaces      : numberOfDecimalsOptions,
        ValidFromDate      : defaultValidFromDate,
        ValidToDate        : defaultValidToDate,
]

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def uomsPerMaterial = uomOptions?.getOrDefault(lnProduct.sku, [])
    uomsPerMaterial?.add("KG")
    uomsPerMaterial?.sort()
    uomsPerMaterial?.unique()

//    def productMasterItem = productMasterData?.get(lnProduct.sku)
//
//    def ph1 = productMasterItem?.PH1Code
//    def shipTo = calculations.getInputValue(lnProduct, lineItemConstants.SHIP_TO_ID)
//
//    def exclusionData = calculations.getPriceProtectionDataByExclusion(exclusionsMap, selectedSoldTo, ph1, shipTo, lnProduct.sku)

    def conditionalParams = [
            sku          : lnProduct?.sku,
            PriceUOM     : uomsPerMaterial,
//            ExclusionData: exclusionData
    ]

    def previousValues = lnProduct.inputs.find {
        lineItemConstants.CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
            "name"    : lineItemConstants.CONFIGURATOR_NAME,
            "label"   : lineItemConstants.CONFIGURATOR_LABEL,
            "type"    : InputType.CONFIGURATOR,
            "url"     : lineItemConstants.CONFIGURATOR_URL,
            "readOnly": false,
            "value"   : previousValues + params + conditionalParams,
    ])
}

return null
