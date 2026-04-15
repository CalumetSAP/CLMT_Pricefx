import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution() || !quoteProcessor.isPrePhase()) return
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def globalUOMConversionMap = out.FindGlobalUOMConversionTable ?: [:]
def uomConversionMap = out.FindUOMConversionTable ?: [:]
def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]

def useConfiguratorValue, configurator, priceTypeAux, priceType, referencePeriod, indexValues, material, pricingUOM, adder, adderUOM, price
def index1, index2, index3, indexHasChanged, index1Calculation, index2Calculation, index3Calculation, indexOneValue, indexTwoValue, indexThreeValue,
        indexOnePercent, indexTwoPercent, indexThreePercent, indexValue
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
    referencePeriod = referencePeriod ? dropdownOptions["ReferencePeriod"]?.find { k, v -> v.toString().startsWith(referencePeriod as String) }?.key : referencePeriod
    indexValues = calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)
    if (priceType != "1" || !referencePeriod || !indexValues) continue

    def numberOfDecimals = calculations.getInputValue(lnProduct, lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: "2"

    material = lnProduct.sku
    Map<String, Map> indexValueCalculatedGrouped = [:]
    pricingUOM = calculations.getInputValue(lnProduct, lineItemConstants.PRICING_UOM_ID)
    adderUOM = calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID)
    index1 = indexValues?.size() > 0 ? indexValues?.get(0) : null
    index2 = indexValues?.size() > 1 ? indexValues?.get(1) : null
    index3 = indexValues?.size() > 2 ? indexValues?.get(2) : null
    indexHasChanged = configurator?.get(lineItemConstants.INDEX_DATA_HAS_CHANGED_ID)
    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)

    if (referencePeriod == "5") {
        indexValueCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, out.LoadLastDaysOfPreviousMonth, adderUOM, uomConversionMap, globalUOMConversionMap, numberOfDecimals)
    } else if (referencePeriod == "6") {
        indexValueCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, out.LoadLastDaysOfPreviousQuarter, adderUOM, uomConversionMap, globalUOMConversionMap, numberOfDecimals)
    } else if(referencePeriod == "11") {
        indexValueCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, out.LoadFirstDaysOfPreviousMonth, adderUOM, uomConversionMap, globalUOMConversionMap, numberOfDecimals)
    } else {
        List filters = []
        def referenceFilters = out.LoadReferencePeriodDateFilter?.get(referencePeriod)
        filters.addAll(referenceFilters)
        filters.addAll(libs.PricelistLib.Index.getIndexValuesKeysFilters(index1, index2, index3))

        def indexValuesGrouped = api.findLookupTableValues("IndexValues", ["key1", "key2", "attribute1", "attribute2", "attribute4", "attribute5"], null, *filters)?.groupBy {
            ("${it.key1}-${it.key2}-${it.attribute1}" as String)
        }
        indexValueCalculatedGrouped = indexValuesGrouped.collectEntries { key, rows -> [
                (key): calculateIndexValueAndConversionAlert(material, rows, adderUOM, uomConversionMap, globalUOMConversionMap, numberOfDecimals)
        ]}
    }

    index1Calculation = indexValueCalculatedGrouped[index1]
    index2Calculation = indexValueCalculatedGrouped[index2]
    index3Calculation = indexValueCalculatedGrouped[index3]

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
            ? (dsData?.get("IndexNumberOnePercent") ?: BigDecimal.ZERO)
            : (indexValues?.size() == 1 ? 1.toBigDecimal() : (indexValues?.size() == 2 ? 0.5.toBigDecimal() : (indexValues?.size() == 3 ? (1/3).toBigDecimal() : BigDecimal.ZERO)))
    indexTwoPercent = !indexHasChanged
            ? (dsData?.get("IndexNumberTwoPercent") ?: BigDecimal.ZERO)
            : (indexValues?.size() == 1 ? 0.toBigDecimal() : (indexValues?.size() == 2 ? 0.5.toBigDecimal() : (indexValues?.size() == 3 ? (1/3).toBigDecimal() : BigDecimal.ZERO)))
    indexThreePercent = !indexHasChanged
            ? (dsData?.get("IndexNumberThreePercent") ?: BigDecimal.ZERO)
            : (indexValues?.size() == 1 ? 0.toBigDecimal() : (indexValues?.size() == 2 ? 0.toBigDecimal() : (indexValues?.size() == 3 ? (1/3).toBigDecimal() : BigDecimal.ZERO)))

    indexValue = (indexOneValue * indexOnePercent) + (indexTwoValue * indexTwoPercent) + (indexThreeValue * indexThreePercent)
    adder = calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_ADDER_ID)
    price = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_ID) ?: BigDecimal.ZERO

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

LinkedHashMap<String, Map> getIndexValueCalculatedGroupedForLastDaysReferencePeriod (String material, String index1, String index2, String index3, lastDaysRows, String adderUOM, uomConversion, globalUOMConversion, numberOfDecimals) {
    LinkedHashMap<String, Map> map = [:]
    if (index1) {
        map.put(index1, calculateIndexValueAndConversionAlert(material, getListOrNull(lastDaysRows?.get(0)), adderUOM, uomConversion, globalUOMConversion, numberOfDecimals))
    }
    if (index2) {
        map.put(index2, calculateIndexValueAndConversionAlert(material, getListOrNull(lastDaysRows?.get(1)), adderUOM, uomConversion, globalUOMConversion, numberOfDecimals))
    }
    if (index3) {
        map.put(index3, calculateIndexValueAndConversionAlert(material, getListOrNull(lastDaysRows?.get(2)), adderUOM, uomConversion, globalUOMConversion, numberOfDecimals))
    }
    return map
}

List getListOrNull (value) {
    return value ? [value] : null
}

Map calculateIndexValueAndConversionAlert (String material, List indexValueRows, String adderUOM, uomConversion, globalUOMConversion, numberOfDecimals) {
    BigDecimal average = libs.PricelistLib.Index.getIndexAverage(indexValueRows)

    String indexValueUOM = indexValueRows?.find()?.attribute5
    BigDecimal indexToAdderConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, indexValueUOM, adderUOM, uomConversion, globalUOMConversion)?.toBigDecimal()

    BigDecimal indexValueInAdderUOM = null
    List<String> conversionAlertMsgs = []

    if (!indexToAdderConversionFactor) {
        conversionAlertMsgs.add("Missing 'UOM conversion' from Index UOM (${indexValueUOM}) to Adder UOM (${adderUOM}) for material ${material}")
    }
    if (!conversionAlertMsgs) {
        indexValueInAdderUOM = average * indexToAdderConversionFactor
        indexValueInAdderUOM = libs.SharedLib.RoundingUtils.round(indexValueInAdderUOM, numberOfDecimals.toInteger())
    }

    return [
            indexValueInAdderUOM: indexValueInAdderUOM,
            conversionAlertMsgs : conversionAlertMsgs,
    ]
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