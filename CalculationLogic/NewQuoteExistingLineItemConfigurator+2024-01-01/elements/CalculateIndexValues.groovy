import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

final indexLib = libs.PricelistLib.Index
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def sdf = new SimpleDateFormat("yyyy-MM-dd")

def priceType = InputPriceType?.input?.getValue()
def referencePeriod = PFReferencePeriod?.entry?.getFirstInput()?.getValue()
def priceCompleted = out.PriceCompletedHidden?.getFirstInput()?.getValue()
def indexValues = PFIndexNumber?.entry?.getFirstInput()?.getValue()
def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"
def priceValidFrom = InputPriceValidFrom?.input?.getValue()
priceValidFrom = priceValidFrom ? sdf.parse(priceValidFrom) : priceValidFrom
def recalculationDate = PFRecalculationDate?.input?.getValue()
String material = InputMaterial?.input?.getValue()
String pricingUOM = InputPricingUOM?.input?.getValue()
String adderUOM = PFAdderUOM?.entry?.getFirstInput()?.getValue()
String index1 = api.local.index1
String index2 = api.local.index2
String index3 = api.local.index3

if(indexLib.validateCalculatedIndexValues(priceType, indexValues, referencePeriod)) return

Map<String, Map> indexValueCalculatedGrouped = [:]

indexValueCalculatedGrouped = indexLib.getIndexByReferencePeriod(priceType, referencePeriod, indexValues, numberOfDecimals, material, priceValidFrom, recalculationDate, adderUOM)

def index1Calculation = indexValueCalculatedGrouped?.get(index1)
def index2Calculation = indexValueCalculatedGrouped?.get(index2)
def index3Calculation = indexValueCalculatedGrouped?.get(index3)

def globalUOMConversionMap = api.local.globalUOMTable ?: [:]
def uomConversionMap = api.local.uomTable ?: [:]

// If no data is available for the specific date. Pull max date data before the targeted Date.
def pullMaxDateData = indexLib.fallIntoSpecificDate(referencePeriod)
if((!index1Calculation || !index2Calculation || !index3Calculation) && pullMaxDateData) {
    indexValueCalculatedGrouped = indexLib.getIndexByReferencePeriod(priceType, referencePeriod, indexValues, numberOfDecimals, material, priceValidFrom, recalculationDate, adderUOM, true)

    index1Calculation = !index1Calculation ? indexValueCalculatedGrouped?.get(index1) : index1Calculation
    index2Calculation = !index2Calculation ? indexValueCalculatedGrouped?.get(index2) : index2Calculation
    index3Calculation = !index3Calculation ? indexValueCalculatedGrouped?.get(index3) : index3Calculation
}

def alerts = []

if (index1Calculation?.conversionAlertMsgs) alerts.add(index1Calculation?.conversionAlertMsgs)
if (index2Calculation?.conversionAlertMsgs) alerts.add(index1Calculation?.conversionAlertMsgs)
if (index3Calculation?.conversionAlertMsgs) alerts.add(index1Calculation?.conversionAlertMsgs)

if (alerts) {
    api.local.alerts = alerts
    return null
}

def indexOneValue = index1Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
def indexTwoValue = index2Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
def indexThreeValue = index3Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
def indexHasChanged = out.InputIndexHasChange.getFirstInput().getValue()
def indexOnePercent = !indexHasChanged
        ? normalizeToThird(api.local.contractData?.IndexNumberOnePercent?.toBigDecimal() ?: BigDecimal.ZERO)
        : (indexValues?.size() == 1 ? 1.toBigDecimal() : (indexValues?.size() == 2 ? 0.5.toBigDecimal() : (indexValues?.size() == 3 ? (1/3).toBigDecimal() : BigDecimal.ZERO)))
def indexTwoPercent = !indexHasChanged
        ? normalizeToThird(api.local.contractData?.IndexNumberTwoPercent?.toBigDecimal() ?: BigDecimal.ZERO)
        : (indexValues?.size() == 1 ? 0.toBigDecimal() : (indexValues?.size() == 2 ? 0.5.toBigDecimal() : (indexValues?.size() == 3 ? (1/3).toBigDecimal() : BigDecimal.ZERO)))
def indexThreePercent = !indexHasChanged
        ? normalizeToThird(api.local.contractData?.IndexNumberThreePercent?.toBigDecimal() ?: BigDecimal.ZERO)
        : (indexValues?.size() == 1 ? 0.toBigDecimal() : (indexValues?.size() == 2 ? 0.toBigDecimal() : (indexValues?.size() == 3 ? (1/3).toBigDecimal() : BigDecimal.ZERO)))

def indexValue = (indexOneValue * indexOnePercent) + (indexTwoValue * indexTwoPercent) + (indexThreeValue * indexThreePercent)
def adder = PFAdder?.entry?.getFirstInput()?.getValue()
def price = InputPrice?.entry?.getFirstInput()?.getValue() ?: BigDecimal.ZERO

// Price Completed validation PRICE_COMPLETED_PRICE_ID || PRICE_COMPLETED_DELIVERED_PRICE_ID
if ((priceCompleted == lineItemConstants.PRICE_COMPLETED_PRICE_ID || priceCompleted == lineItemConstants.PRICE_COMPLETED_DELIVERED_PRICE_ID) &&
        (api.local.priceHasChanged || api.local.deliveredPriceHasChanged || api.local.freightAmountHasChanged || api.local.indexHasChanged || api.local.recalculationDateHasChanged || api.local.priceValidFromHasChanged)) {
    def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, pricingUOM, adderUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
    if (!conversionFactor) {
        alerts.add("Missing 'UOM conversion' from Pricing UOM (${pricingUOM}) to Adder UOM (${adderUOM}) for material ${material}")
        api.local.alerts = alerts
        return null
    }
    def convertedPrice = price * conversionFactor
    def result = libs.SharedLib.RoundingUtils.round(convertedPrice - indexValue, numberOfDecimals.toInteger())
    PFAdder?.entry?.getFirstInput()?.setValue(result)
    out.HiddenValues?.getInputs()?.find { it.name == PFAdder?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(result)
}

if (priceCompleted == lineItemConstants.PRICE_COMPLETED_ADDER_ID && (api.local.adderHasChanged|| api.local.freightAmountHasChanged || api.local.indexHasChanged || api.local.recalculationDateHasChanged || api.local.priceValidFromHasChanged)) {
    def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, adderUOM, pricingUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
    if (!conversionFactor) {
        alerts.add("Missing 'UOM conversion' from Adder UOM (${adderUOM}) to Pricing UOM (${pricingUOM}) for material ${material}")
        api.local.alerts = alerts
        return null
    }
    def convertedAdder = adder * conversionFactor
    def convertedIndex = indexValue * conversionFactor

    def result = libs.QuoteLibrary.RoundingUtils.round(convertedIndex + convertedAdder, numberOfDecimals?.toInteger())
    InputPrice?.entry?.getFirstInput()?.setValue(result)
    out.HiddenValues?.getInputs()?.find { it.name == InputPrice?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(result)

    def freightAmount = InputFreightAmount?.entry?.getFirstInput()?.getValue() ?: BigDecimal.ZERO
    def freightUOM = InputFreightUOM?.input?.getValue()

    def convertedFreight = freightAmount
    if (freightAmount && freightUOM) {
        conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightUOM, pricingUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
        if (!conversionFactor) {
            api.local.freightAlert = "Missing 'UOM conversion' from Adder UOM (${freightUOM}) to Pricing UOM (${pricingUOM}) for material ${material}"
            return null
        }
        convertedFreight = freightAmount * conversionFactor
    }

    def deliveredPrice = libs.QuoteLibrary.RoundingUtils.round(result + convertedFreight, numberOfDecimals?.toInteger())

    InputDeliveredPrice?.entry?.getFirstInput()?.setValue(deliveredPrice)
    out.HiddenValues?.getInputs()?.find { it.name == InputDeliveredPrice?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(deliveredPrice)
}

return null

BigDecimal normalizeToThird(BigDecimal value) {
    def str = value?.stripTrailingZeros()?.toPlainString()
    if (["0.33", "0.333", "0.3333", "0.3333"].contains(str)) {
        return 1/3.toBigDecimal()
    }
    return value
}