String material = api.local.material

Map<String, Map> indexValueCalculatedGrouped = [:]

final indexLib = libs.PricelistLib.Index
def numberOfDecimals = api.local.numberOfDecimals

String productUOM = out.LoadProducts?.UOM
String referencePeriod = api.local.referencePeriod
String adderUOM = api.local.adderUOM

String index1 = api.local.index1
String index2 = api.local.index2
String index3 = api.local.index3
if (referencePeriod == "5") {
    indexValueCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, out.LoadLastDaysOfPreviousMonth, productUOM, adderUOM, api.global.uomConversion, api.global.globalUOMConversion, numberOfDecimals)
} else if (referencePeriod == "6") {
    indexValueCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, out.LoadLastDaysOfPreviousQuarter, productUOM, adderUOM, api.global.uomConversion, api.global.globalUOMConversion, numberOfDecimals)
} else if(referencePeriod == "11") {
    indexValueCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, out.LoadFirstDaysOfPreviousMonth, productUOM, adderUOM, api.global.uomConversion, api.global.globalUOMConversion, numberOfDecimals)
} else {
    List filters = []
    filters.addAll(out.LoadReferencePeriodDateFilter)
    filters.addAll(libs.PricelistLib.Index.getIndexValuesKeysFilters(index1, index2, index3))

    def indexValuesGrouped = api.findLookupTableValues("IndexValues", ["key1", "key2", "attribute1", "attribute2", "attribute4", "attribute5"], null, *filters)?.groupBy {
        ("${it.key1}-${it.key2}-${it.attribute1}" as String)
    }
    indexValueCalculatedGrouped = indexValuesGrouped.collectEntries { key, rows -> [
            (key): calculateIndexValueAndConversionAlert(material, rows, productUOM, adderUOM, api.global.uomConversion, api.global.globalUOMConversion, numberOfDecimals)
    ]}
}

def indexValueReCalculatedGrouped

if((!indexValueCalculatedGrouped[index1] || !indexValueCalculatedGrouped[index2] || !indexValueCalculatedGrouped[index3])) {
    if (referencePeriod == "5") {
        indexValueReCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, out.LoadLastDaysOfPreviousMonth, productUOM, adderUOM, api.global.uomConversion, api.global.globalUOMConversion, numberOfDecimals)
    } else if (referencePeriod == "6") {
        indexValueReCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, out.LoadLastDaysOfPreviousQuarter, productUOM, adderUOM, api.global.uomConversion, api.global.globalUOMConversion, numberOfDecimals)
    } else if(referencePeriod == "11") {
        indexValueReCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, out.LoadFirstDaysOfPreviousMonth, productUOM, adderUOM, api.global.uomConversion, api.global.globalUOMConversion, numberOfDecimals)
    }else if((referencePeriod == "7" || referencePeriod == "8")) {
        List filters = []
        if(referencePeriod == "7") filters.addAll(indexLib.getThirdWednesdayOfMonthFilters(api.global.calculationDate, true))
        if(referencePeriod == "8") filters.addAll(indexLib.getFourthWednesdayOfMonthFilters(api.global.calculationDate, true))

        filters.addAll(libs.PricelistLib.Index.getIndexValuesKeysFilters(index1, index2, index3))

        def indexValuesGrouped = api.findLookupTableValues("IndexValues", ["key1", "key2", "attribute1", "attribute2", "attribute4", "attribute5"], "-key3", *filters)?.groupBy {
            ("${it.key1}-${it.key2}-${it.attribute1}" as String)
        }

        indexValueReCalculatedGrouped = indexValuesGrouped.collectEntries { key, rows ->
            if(indexLib.fallIntoSpecificDate(referencePeriod)) rows = [rows.find{it}]
                    [
                (key): calculateIndexValueAndConversionAlert(material, rows, productUOM, adderUOM, api.global.uomConversion, api.global.globalUOMConversion, numberOfDecimals)
            ]}
    }
}

api.local.index1Calculation = index1 ? !indexValueCalculatedGrouped?.get(index1) && indexValueReCalculatedGrouped?.get(index1) ? indexValueReCalculatedGrouped?.get(index1) : indexValueCalculatedGrouped?.get(index1) : null
api.local.index2Calculation = index2 ? !indexValueCalculatedGrouped?.get(index2) && indexValueReCalculatedGrouped?.get(index2) ? indexValueReCalculatedGrouped?.get(index2) : indexValueCalculatedGrouped?.get(index2) : null
api.local.index3Calculation = index3 ? !indexValueCalculatedGrouped?.get(index3) && indexValueReCalculatedGrouped?.get(index3) ? indexValueReCalculatedGrouped?.get(index3) : indexValueCalculatedGrouped?.get(index3) : null

return null

LinkedHashMap<String, Map> getIndexValueCalculatedGroupedForLastDaysReferencePeriod (String material, String index1, String index2, String index3, lastDaysRows, String productUOM, String adderUOM, uomConversion, globalUOMConversion, numberOfDecimals) {
    LinkedHashMap<String, Map> map = [:]
    if (index1) {
        map.put(index1, calculateIndexValueAndConversionAlert(material, getListOrNull(lastDaysRows?.get(0)), productUOM, adderUOM, uomConversion, globalUOMConversion, numberOfDecimals))
    }
    if (index2) {
        map.put(index2, calculateIndexValueAndConversionAlert(material, getListOrNull(lastDaysRows?.get(1)), productUOM, adderUOM, uomConversion, globalUOMConversion, numberOfDecimals))
    }
    if (index3) {
        map.put(index3, calculateIndexValueAndConversionAlert(material, getListOrNull(lastDaysRows?.get(2)), productUOM, adderUOM, uomConversion, globalUOMConversion, numberOfDecimals))
    }
    return map
}

List getListOrNull (value) {
    return value ? [value] : null
}

Map calculateIndexValueAndConversionAlert (String material, List indexValueRows, String productUOM, String adderUOM, uomConversion, globalUOMConversion, numberOfDecimals) {
    BigDecimal average = libs.PricelistLib.Index.getIndexAverage(indexValueRows)

    String indexValueUOM = indexValueRows?.find()?.attribute5
    BigDecimal indexToProductConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, indexValueUOM, productUOM, uomConversion, globalUOMConversion)?.toBigDecimal()
    BigDecimal productToAdderConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, productUOM, adderUOM, uomConversion, globalUOMConversion)?.toBigDecimal()

    BigDecimal indexValueInAdderUOM = null
    List<String> conversionAlertMsgs = []

    if (!indexToProductConversionFactor) {
        conversionAlertMsgs.add("Missing 'UOM conversion' from Index UOM (${indexValueUOM}) to Product UOM (${productUOM}) for material ${material}")
    }
    if (!productToAdderConversionFactor) {
        conversionAlertMsgs.add("Missing 'UOM conversion' from Product UOM (${productUOM}) to Adder UOM (${adderUOM}) for material ${material}")
    }
    if (!conversionAlertMsgs) {
        indexValueInAdderUOM = average * indexToProductConversionFactor * productToAdderConversionFactor
        indexValueInAdderUOM = libs.SharedLib.RoundingUtils.round(indexValueInAdderUOM, numberOfDecimals.toInteger())
    }

    return [
            indexValueInAdderUOM: indexValueInAdderUOM,
            conversionAlertMsgs : conversionAlertMsgs,
    ]
}