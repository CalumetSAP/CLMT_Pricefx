import net.pricefx.common.api.InputType

import java.text.SimpleDateFormat

if (api.isInputGenerationExecution() || !quoteProcessor.isPrePhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final indexLib = libs.PricelistLib.Index
def sdf = new SimpleDateFormat("yyyy-MM-dd")

def globalUOMConversionMap = out.FindGlobalUOMConversionTable ?: [:]
def uomConversionMap = out.FindUOMConversionTable ?: [:]
def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]

def useConfiguratorValue, configurator, priceTypeAux, priceType, referencePeriod, indexValues, material, pricingUOM, adder, adderUOM, price
def index1, index2, index3, indexHasChanged, index1Calculation, index2Calculation, index3Calculation, indexOneValue, indexTwoValue, indexThreeValue,
        indexOnePercent, indexTwoPercent, indexThreePercent, indexValue, priceValidFrom, recalculationDate
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    if (!api.local.priceHasChanged?.get(lnProduct.lineId) && !api.local.deliveredPriceHasChanged?.get(lnProduct.lineId)
            && !api.local.adderHasChanged?.get(lnProduct.lineId)) continue

    useConfiguratorValue = api.local.lineItemChanged == lnProduct.lineId

    configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    priceTypeAux = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
    priceType = useConfiguratorValue
            ? configurator?.get(lineItemConstants.PRICE_TYPE_ID)
            : priceTypeAux ? dropdownOptions["PriceType"]?.find { k, v -> v.toString().startsWith(priceTypeAux) }?.key : priceTypeAux
    referencePeriod = calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)
    referencePeriod = useConfiguratorValue
            ? configurator?.get(lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)
            : referencePeriod ? dropdownOptions["ReferencePeriod"]?.find { k, v -> v.toString().startsWith(referencePeriod as String) }?.key : referencePeriod
    indexValues = findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, lnProduct, configurator)
    if (priceType != "1" || !referencePeriod || !indexValues) continue

    api.local.recalculationDateHasChanged = true

    priceValidFrom = findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)
    priceValidFrom = priceValidFrom ? sdf.parse(priceValidFrom) : priceValidFrom
    recalculationDate = findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID, lnProduct, configurator)

    def numberOfDecimals = findValue(useConfiguratorValue, lineItemConstants.NUMBER_OF_DECIMALS_ID, lnProduct, configurator) ?: "2"

    material = lnProduct.sku
    pricingUOM = findValue(useConfiguratorValue, lineItemConstants.PRICING_UOM_ID, lnProduct, configurator)
    adderUOM = findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID, lnProduct, configurator)
    index1 = indexValues?.size() > 0 ? indexValues?.get(0) : null
    index2 = indexValues?.size() > 1 ? indexValues?.get(1) : null
    index3 = indexValues?.size() > 2 ? indexValues?.get(2) : null
    api.local.index1 = index1
    api.local.index2 = index2
    api.local.index3 = index3
    indexHasChanged = configurator?.get(lineItemConstants.INDEX_DATA_HAS_CHANGED_ID)
    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)

    Map<String, Map> indexValueCalculatedGrouped = [:]

    indexValueCalculatedGrouped = indexLib.getIndexByReferencePeriod(priceType, referencePeriod, indexValues, numberOfDecimals, material, priceValidFrom, recalculationDate, adderUOM)

    index1Calculation = indexValueCalculatedGrouped?.get(index1)
    index2Calculation = indexValueCalculatedGrouped?.get(index2)
    index3Calculation = indexValueCalculatedGrouped?.get(index3)

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
        continue
    }

    indexOneValue = index1Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
    indexTwoValue = index2Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
    indexThreeValue = index3Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO

    indexOnePercent = !indexHasChanged
            ? normalizeToThird(dsData?.get("IndexNumberOnePercent") ?: BigDecimal.ZERO)
            : (indexValues?.size() == 1 ? 1.toBigDecimal() : (indexValues?.size() == 2 ? 0.5.toBigDecimal() : (indexValues?.size() == 3 ? (1/3).toBigDecimal() : BigDecimal.ZERO)))
    indexTwoPercent = !indexHasChanged
            ? normalizeToThird(dsData?.get("IndexNumberTwoPercent") ?: BigDecimal.ZERO)
            : (indexValues?.size() == 1 ? 0.toBigDecimal() : (indexValues?.size() == 2 ? 0.5.toBigDecimal() : (indexValues?.size() == 3 ? (1/3).toBigDecimal() : BigDecimal.ZERO)))
    indexThreePercent = !indexHasChanged
            ? normalizeToThird(dsData?.get("IndexNumberThreePercent") ?: BigDecimal.ZERO)
            : (indexValues?.size() == 1 ? 0.toBigDecimal() : (indexValues?.size() == 2 ? 0.toBigDecimal() : (indexValues?.size() == 3 ? (1/3).toBigDecimal() : BigDecimal.ZERO)))

    indexValue = (indexOneValue * indexOnePercent) + (indexTwoValue * indexTwoPercent) + (indexThreeValue * indexThreePercent)
    adder = findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_ADDER_ID, lnProduct, configurator)
    price = findValue(useConfiguratorValue, lineItemConstants.PRICE_ID, lnProduct, configurator) ?: BigDecimal.ZERO

    if (api.local.priceHasChanged?.get(lnProduct.lineId) || api.local.deliveredPriceHasChanged?.get(lnProduct.lineId)) {
        def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, pricingUOM, adderUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
        if (!conversionFactor) {
            alerts.add("Missing 'UOM conversion' from Pricing UOM (${pricingUOM}) to Adder UOM (${adderUOM}) for material ${material}")
            api.local.alerts = alerts
            continue
        }
        def convertedPrice = price * conversionFactor
        def result = libs.SharedLib.RoundingUtils.round(convertedPrice - indexValue, numberOfDecimals.toInteger())
        updateValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_ADDER_ID, result)
        updateHiddenValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_ADDER_ID + "Previous", result)
    }

    if (api.local.adderHasChanged?.get(lnProduct.lineId)) {
        def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, adderUOM, pricingUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
        if (!conversionFactor) {
            alerts.add("Missing 'UOM conversion' from Adder UOM (${adderUOM}) to Pricing UOM (${pricingUOM}) for material ${material}")
            api.local.alerts = alerts
            continue
        }
        def convertedAdder = adder * conversionFactor
        def convertedIndex = indexValue * conversionFactor

        def result = libs.QuoteLibrary.RoundingUtils.round(convertedIndex + convertedAdder, numberOfDecimals?.toInteger())
        updateValue(lnProduct.lineId, lineItemConstants.PRICE_ID, result)
        updateHiddenValue(lnProduct.lineId, lineItemConstants.PRICE_ID + "Previous", result)

        def deliveredPrice = libs.QuoteLibrary.RoundingUtils.round(result, numberOfDecimals?.toInteger())
        updateValue(lnProduct.lineId, lineItemConstants.DELIVERED_PRICE_ID, deliveredPrice)
        updateHiddenValue(lnProduct.lineId, lineItemConstants.DELIVERED_PRICE_ID + "Previous", deliveredPrice)

        updateHiddenValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_ADDER_ID + "Previous", adder)
    }

}

return null

List getListOrNull (value) {
    return value ? [value] : null
}

def updateValue(String lineId, name, value) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : value,
    ])
}

def updateHiddenValue(String lineId, name, value) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : value,
            "type"        : InputType.HIDDEN,
    ])
}

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}

BigDecimal normalizeToThird(BigDecimal value) {
    def str = value?.stripTrailingZeros()?.toPlainString()
    if (["0.33", "0.333", "0.3333", "0.3333"].contains(str)) {
        return 1/3.toBigDecimal()
    }
    return value
}