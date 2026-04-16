import groovy.transform.Field

import java.text.SimpleDateFormat

@Field final List<Integer> FIRSTS_MONTHS_OF_QUARTERS = [0, 3, 6, 9]
@Field final List<Integer> SECONDS_MONTHS_OF_QUARTERS = [1, 4, 7, 10]
@Field final String DEV_PARTITION = "calumet-dev"
//@Field final List<Integer> THIRDS_MONTHS_OF_QUARTERS = [2, 5, 8, 11] //THIS IS UNUSED

def getTodayOrBusinessRules(priceValidFrom, recalculationDate) {
    if(api.currentPartitionName() == DEV_PARTITION) {
        return getBusinessRulesTodayDate()
    } else {
        def today = libs.QuoteLibrary.DateUtils.getToday()
        def auxDate = priceValidFrom > today ? today : priceValidFrom

        if (!recalculationDate) return auxDate

        def calendar = Calendar.getInstance()
        calendar.setTime(auxDate)
        def day = calendar.get(Calendar.DAY_OF_MONTH)
        def recalculationDay = recalculationDate?.toInteger()

        def finalDate
        if (priceValidFrom < today) {
            if (day < recalculationDay) {
                def newCalendar = Calendar.getInstance()
                newCalendar.setTime(auxDate)
                newCalendar.add(Calendar.MONTH, -1)
                newCalendar.set(Calendar.DAY_OF_MONTH, recalculationDay)
                finalDate = newCalendar.getTime()
            } else {
                def newCalendar = Calendar.getInstance()
                newCalendar.setTime(auxDate)
                newCalendar.set(Calendar.DAY_OF_MONTH, recalculationDay)
                finalDate = newCalendar.getTime()
            }
        } else {
            if (day < recalculationDay) {
                def newCalendar = Calendar.getInstance()
                newCalendar.setTime(auxDate)
                newCalendar.add(Calendar.MONTH, -1)
                newCalendar.set(Calendar.DAY_OF_MONTH, recalculationDay)
                finalDate = newCalendar.getTime()
            } else {
                def newCalendar = Calendar.getInstance()
                newCalendar.setTime(auxDate)
                finalDate = newCalendar.getTime()
            }
        }

        return finalDate
    }
}

def getRecalculationDate(priceValidFrom, recalculationDateValue, recalculationPeriod) {
    if (!recalculationDateValue) return priceValidFrom

    def calendar = Calendar.getInstance()
    calendar.setTime(priceValidFrom)
    def day = calendar.get(Calendar.DAY_OF_MONTH)
    def month = calendar.get(Calendar.MONTH)
    def recalculationDay = recalculationDateValue?.toInteger()

    def newCalendar = Calendar.getInstance()
    newCalendar.setTime(priceValidFrom)
    newCalendar.set(Calendar.DAY_OF_MONTH, recalculationDay)

    if ((recalculationPeriod == "Quarter" && (!FIRSTS_MONTHS_OF_QUARTERS.contains(month) || (FIRSTS_MONTHS_OF_QUARTERS.contains(month) && day > recalculationDay)))
            || day > recalculationDay) {
        newCalendar.add(Calendar.MONTH, getNextPeriod(recalculationPeriod, month))
    }

    return newCalendar.getTime()
}

int getNextPeriod(String recalculationPeriod, int currentMonth)  {
    if (recalculationPeriod == "Month") return 1

    if (recalculationPeriod == "Quarter") {
        int[] quarters = [0, 3, 6, 9]

        Integer nextQuarterMonth = quarters.find { it > currentMonth }
        if (nextQuarterMonth == null) {
            nextQuarterMonth = 0
        }

        return nextQuarterMonth - currentMonth
    }

    return 1
}

Date getBusinessRulesTodayDate () {
    String today = api.findLookupTableValues("BusinessRules", ["attribute1"], null, Filter.equal("name", "today"))?.find()?.attribute1
    if (today) {
        return new SimpleDateFormat("yyyy-MM-dd").parse(today)
    }
    return null
}

List getPreviousMonthAverageFilters (Date referenceDate) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(referenceDate)
    calendar.add(Calendar.MONTH, -1)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    Date firstDayOfPreviousMonth = calendar.getTime()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    Date lastDayOfPreviousMonth = calendar.getTime()

    return buildDateRangeFilters(firstDayOfPreviousMonth, lastDayOfPreviousMonth)
}

List getFirstDayMonthFilter (Date referenceDate) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(referenceDate)
    calendar.add(Calendar.MONTH, -1)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    Date firstDayOfPreviousMonth = calendar.getTime()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

    return buildMaxDateDataFilter(firstDayOfPreviousMonth)
}

List getLastDayMonthFilter (Date referenceDate) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(referenceDate)
    calendar.add(Calendar.MONTH, -1)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    Date lastDayOfPreviousMonth = calendar.getTime()

    return buildMaxDateDataFilter(lastDayOfPreviousMonth)
}

List getMidMonthAverageFilters (Date referenceDate, Integer fromDay=16, Integer toDay=15) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(referenceDate)
    Integer day = calendar.get(Calendar.DAY_OF_MONTH)
    if (day <= toDay) {
        calendar.add(Calendar.MONTH, -1)
    }
    calendar.set(Calendar.DAY_OF_MONTH, toDay)
    Date dateTo = calendar.getTime()
    calendar.add(Calendar.MONTH, -1)
    calendar.set(Calendar.DAY_OF_MONTH, fromDay)
    Date dateFrom = calendar.getTime()

    return buildDateRangeFilters(dateFrom, dateTo)
}

def getThirdWednesdayOfMonthFilters(dateReference = new Date(), loadMaxDateData = false) {
    def calendar = Calendar.getInstance()
    calendar.time = dateReference

    // Get third wednesday of the month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    calendar.add(Calendar.DAY_OF_MONTH, 14) // Forward 2 weeks

    def thirdWednesday = calendar.time

    // If reference date is before Wednesday day of third month
    if (thirdWednesday > (dateReference as Date)) {
        // Get third wednesday of the month
        calendar.add(Calendar.MONTH, -1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar.add(Calendar.DAY_OF_MONTH, 14) // Forward 2 weeks
        thirdWednesday = calendar.time
    }

    if(loadMaxDateData) {
        return buildMaxDateDataFilter(thirdWednesday)
    }

    return buildDateRangeFilters(thirdWednesday, thirdWednesday)
}

def getFourthWednesdayOfMonthFilters(dateReference = new Date(), loadMaxDateData = false) {
    def calendar = Calendar.getInstance()
    calendar.time = dateReference

    // Get fourth wednesday of the month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    calendar.add(Calendar.DAY_OF_MONTH, 21) // Forward 3 weeks

    def fourthWednesday = calendar.time

    // If reference date is before Wednesday day of fourth month
    if (fourthWednesday > (dateReference as Date)) {
        // Get fourth wednesday of the month
        calendar.add(Calendar.MONTH, -1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar.add(Calendar.DAY_OF_MONTH, 21) // Forward 3 weeks
        fourthWednesday = calendar.time
    }

    if(loadMaxDateData) {
        return buildMaxDateDataFilter(fourthWednesday)
    }

    return buildDateRangeFilters(fourthWednesday, fourthWednesday)
}

List getPreviousQuarterAverageFilters (Date referenceDate) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(referenceDate)
    Integer monthIndex = calendar.get(Calendar.MONTH)
    if (FIRSTS_MONTHS_OF_QUARTERS.contains(monthIndex)) {
        calendar.add(Calendar.MONTH, -1)
    } else if (SECONDS_MONTHS_OF_QUARTERS.contains(monthIndex)) {
        calendar.add(Calendar.MONTH, -2)
    } else {
        calendar.add(Calendar.MONTH, -3)
    }
    // method -> getMonthsToSubstrate(monthIndex) -> calendar.add(Calendar.MONTH, getMonthsToSubstrate(monthIndex))
    // -----------------------------------
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    Date lastDayOfQuarter = calendar.getTime()
    calendar.add(Calendar.MONTH, -2)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    Date firstDayOfQuarter = calendar.getTime()

    return buildDateRangeFilters(firstDayOfQuarter, lastDayOfQuarter)
}

def getLastDayOfQuarterFilter (Date referenceDate) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(referenceDate)
    Integer monthIndex = calendar.get(Calendar.MONTH)
    if (FIRSTS_MONTHS_OF_QUARTERS.contains(monthIndex)) {
        calendar.add(Calendar.MONTH, -1)
    } else if (SECONDS_MONTHS_OF_QUARTERS.contains(monthIndex)) {
        calendar.add(Calendar.MONTH, -2)
    } else {
        calendar.add(Calendar.MONTH, -3)
    }
    // method -> getMonthsToSubstrate(monthIndex) -> calendar.add(Calendar.MONTH, getMonthsToSubstrate(monthIndex))
    // -----------------------------------
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    Date lastDayOfQuarter = calendar.getTime()
    calendar.add(Calendar.MONTH, -2)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    return buildMaxDateDataFilter(lastDayOfQuarter)
}

List getRollingQuarterAverageFilters (Date referenceDate) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(referenceDate)

    calendar.add(Calendar.MONTH, -1)
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    Date dateTo = calendar.getTime()

    calendar.add(Calendar.MONTH, -2)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    Date dateFrom = calendar.getTime()

    return buildDateRangeFilters(dateFrom, dateTo)
}

private List buildDateRangeFilters(Date dateFrom, Date dateTo) {
    return [
            Filter.greaterOrEqual("key3", dateFrom),
            Filter.lessOrEqual("key3", dateTo),
    ]
}

private List buildMaxDateDataFilter(Date dateFrom) {
    return [Filter.lessOrEqual("key3", dateFrom)]
}

LinkedHashMap<String, String> getIndexValuesKeys (String index) {
    List<String> splitted = index?.split("-")?.toList()

    def quotationSource = null
    def quotationType = null
    def descOfPriceQuote = null

    while (splitted?.size()){
        if (!quotationSource) {
            quotationSource = splitted.remove(0)
        } else if (!quotationType) {
            quotationType = splitted.remove(0)
        } else if (!descOfPriceQuote) {
            descOfPriceQuote = splitted.remove(0)
        } else {
            descOfPriceQuote += "-" + splitted.remove(0)
        }
    }

    if (quotationSource && quotationType && descOfPriceQuote) {
        return [
                "key1"      : quotationSource,
                "key2"      : quotationType,
                "attribute1": descOfPriceQuote,
        ]
    }
    return [:]
}

List getIndexValuesKeysFilters (String index1, String index2, String index3) {
    Set<String> key1s = new HashSet<>()
    Set<String> key2s = new HashSet<>()
    Set<String> attribute1s = new HashSet<>()

    if (index1) {
        LinkedHashMap<String, String> index1Keys = getIndexValuesKeys(index1)
        key1s.add(index1Keys.key1)
        key2s.add(index1Keys.key2)
        attribute1s.add(index1Keys.attribute1)
    } else {
        return []
    }
    if (index2) {
        LinkedHashMap<String, String> index2Keys = getIndexValuesKeys(index2)
        key1s.add(index2Keys.key1)
        key2s.add(index2Keys.key2)
        attribute1s.add(index2Keys.attribute1)
    }
    if (index3) {
        LinkedHashMap<String, String> index3Keys = getIndexValuesKeys(index3)
        key1s.add(index3Keys.key1)
        key2s.add(index3Keys.key2)
        attribute1s.add(index3Keys.attribute1)
    }

    return [
            Filter.in("key1", key1s),
            Filter.in("key2", key2s),
            Filter.in("attribute1", attribute1s),
    ]
}

BigDecimal getIndexAverage (List indexValueRows) {
    Integer roundingDecimals = libs.PricelistLib.Constants.getPerRoundingDecimals(indexValueRows?.find()?.attribute4?.toString())
    if (!roundingDecimals) return null

    BigDecimal average = indexValueRows ?
            indexValueRows.sum { it.attribute2?.toBigDecimal() ?: BigDecimal.ZERO } / indexValueRows.sum { it.attribute4?.toBigDecimal() ?: BigDecimal.ONE } :
            BigDecimal.ZERO

    return libs.SharedLib.RoundingUtils.round(average, roundingDecimals)
}


def getIndexGroupedByKey(indexValues, List calculationDateFilter, String sortBy) {

    LinkedHashMap<String, HashSet<String>> keys = buildIndexKeyMap(indexValues)
    LinkedHashMap<String, Object> values = buildIndexValuesMap()

    List filters = [
            Filter.equal("lookupTable.name", "IndexValues"),
            Filter.equal("lookupTable.status", "Active"),
            Filter.in("key1", keys["key1s"]),
            Filter.in("key2", keys["key2s"]),
            Filter.in("attribute1", keys["attribute1s"]),
    ]

    filters.addAll(calculationDateFilter)

    def result = api.findLookupTableValues("IndexValues", ["key1", "key2", "key3", "attribute1", "attribute2", "attribute4", "attribute5"], sortBy, *filters)?.groupBy {
        ("${it.key1}-${it.key2}-${it.attribute1}" as String)
    }?.collectEntries { key, indexValueRows ->
        [(key): indexValueRows?.find() ?: [:]]
    } ?: [:]

    api.logWarn("result", result)
    api.logWarn("RESULT", [
            result?.get(values?.index1),
            result?.get(values?.index2),
            result?.get(values?.index3)
    ])

    return [
            result?.get(values?.index1),
            result?.get(values?.index2),
            result?.get(values?.index3)
    ]
}

LinkedHashMap<String, HashSet<String>> buildIndexKeyMap(indexValues) {
    Set<String> key1s = new HashSet<>()
    Set<String> key2s = new HashSet<>()
    Set<String> attribute1s = new HashSet<>()

    LinkedHashMap<String, String> indexValuesKeys

    for (index in indexValues) {
        indexValuesKeys = getIndexValuesKeys(index)
        key1s.add(indexValuesKeys.key1)
        key2s.add(indexValuesKeys.key2)
        attribute1s.add(indexValuesKeys.attribute1)
    }

    return [
            key1s: key1s,
            key2s: key2s,
            attribute1s: attribute1s
    ]
}

LinkedHashMap<String, Object> buildIndexValuesMap() {
    return [
            index1: api.local.index1,
            index2: api.local.index2,
            index3: api.local.index3
    ]
}

Boolean validateOnloadingOfDaysFilters(priceType, indexValues, selectedReferencePeriod, validateReferencePeriod) {
    return validateCalculatedIndexValues(priceType, indexValues, selectedReferencePeriod) || selectedReferencePeriod != validateReferencePeriod
}

Boolean validateCalculatedIndexValues(priceType, indexValues, referencePeriod) {
    return priceType != "1" || !referencePeriod || !(api.local.indexHasChanged || api.local.priceHasChanged || api.local.adderHasChanged || api.local.freightAmountHasChanged || api.local.recalculationDateHasChanged || api.local.priceValidFromHasChanged) || !indexValues
}

Boolean validateReferencePeriod(priceType, referencePeriod) {
    return priceType != "1" || !(api.local.indexHasChanged || api.local.priceHasChanged || api.local.adderHasChanged || api.local.freightAmountHasChanged || api.local.recalculationDateHasChanged) || !referencePeriod
}

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

def getIndexByReferencePeriod(priceType, referencePeriod, indexValues, numberOfDecimals, material, priceValidFrom, recalculationDate, adderUOM, loadMaxDateData = false) {
    Map<String, Map> indexValueCalculatedGrouped = [:]

    String index1 = api.local.index1
    String index2 = api.local.index2
    String index3 = api.local.index3

    def globalUOMConversionMap = api.local.globalUOMTable ?: [:]
    def uomConversionMap = api.local.uomTable ?: [:]

    if (referencePeriod == "5") {
        indexValueCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, loadLastDaysOfPreviousMonth(priceType, indexValues, referencePeriod, priceValidFrom, recalculationDate, "5"), adderUOM, uomConversionMap, globalUOMConversionMap, numberOfDecimals)
    } else if (referencePeriod == "6") {
        indexValueCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, loadLastDaysOfPreviousQuarter(priceType, indexValues, referencePeriod, priceValidFrom, recalculationDate, "6"), adderUOM, uomConversionMap, globalUOMConversionMap, numberOfDecimals)
    } else if(referencePeriod == "11") {
        indexValueCalculatedGrouped = getIndexValueCalculatedGroupedForLastDaysReferencePeriod(material, index1, index2, index3, loadFirstDaysOfPreviousMonth(priceType, indexValues, referencePeriod, priceValidFrom, recalculationDate, "11"), adderUOM, uomConversionMap, globalUOMConversionMap, numberOfDecimals)
    } else {
        List filters = []
        filters.addAll(loadReferencePeriodDateFilter(priceType, referencePeriod, priceValidFrom, recalculationDate, loadMaxDateData))
        filters.addAll(getIndexValuesKeysFilters(index1, index2, index3))

        def sortBy = null

        if(loadMaxDateData) sortBy = "-key3"

        def indexValuesGrouped = api.findLookupTableValues("IndexValues", ["key1", "key2", "key3", "attribute1", "attribute2", "attribute4", "attribute5"], sortBy, *filters)

        if(loadMaxDateData) {
            def groupedTemp = indexValuesGrouped.groupBy { row ->
                ("${row.key1}-${row.key2}-${row.attribute1}" as String)
            }

            indexValuesGrouped = groupedTemp.collect { k, rows ->
                rows.find { it }
            }
        }

        api.logWarn("indexValuesGrouped", indexValuesGrouped)

        indexValuesGrouped = indexValuesGrouped?.groupBy {
            ("${it.key1}-${it.key2}-${it.attribute1}" as String)
        }

        indexValueCalculatedGrouped = indexValuesGrouped.collectEntries { key, rows -> [
                (key): calculateIndexValueAndConversionAlert(material, rows, adderUOM, uomConversionMap, globalUOMConversionMap, numberOfDecimals)
        ]}
    }

    return indexValueCalculatedGrouped

}

def loadLastDaysOfPreviousMonth(priceType, indexValues, selectedReferencePeriod, priceValidFrom, recalculationDate, comparableReferencePeriod = "5") {

    if(validateOnloadingOfDaysFilters(priceType, indexValues, selectedReferencePeriod, comparableReferencePeriod)) return

    Date calculationDate = getTodayOrBusinessRules(priceValidFrom, recalculationDate)
    def sortBy = "-key3"

    return getIndexGroupedByKey(indexValues, getLastDayMonthFilter(calculationDate), sortBy)
}

def loadLastDaysOfPreviousQuarter(priceType, indexValues, selectedReferencePeriod, priceValidFrom, recalculationDate, comparableReferencePeriod = "6") {
    if(validateOnloadingOfDaysFilters(priceType, indexValues, selectedReferencePeriod, comparableReferencePeriod)) return
    Date calculationDate = getTodayOrBusinessRules(priceValidFrom, recalculationDate)
    def sortBy = "-key3"

    return getIndexGroupedByKey(indexValues, getLastDayOfQuarterFilter(calculationDate), sortBy)
}

def loadFirstDaysOfPreviousMonth(priceType, indexValues, selectedReferencePeriod, priceValidFrom, recalculationDate, comparableReferencePeriod = "11") {
    if(validateOnloadingOfDaysFilters(priceType, indexValues, selectedReferencePeriod, comparableReferencePeriod)) return
    Date calculationDate = getTodayOrBusinessRules(priceValidFrom, recalculationDate)
    def sortBy = "-key3"

    return getIndexGroupedByKey(indexValues, getFirstDayMonthFilter(calculationDate), sortBy)
}

def loadReferencePeriodDateFilter(priceType, referencePeriod, priceValidFrom, recalculationDate, loadMaxDateData = false) {

    validateReferencePeriod(priceType, referencePeriod)

    Date calculationDate = getTodayOrBusinessRules(priceValidFrom, recalculationDate)

    switch (referencePeriod) {
        case "1":
            return getPreviousMonthAverageFilters(calculationDate)
        case "2":
            return getMidMonthAverageFilters(calculationDate)
        case "3":
            return getPreviousQuarterAverageFilters(calculationDate)
        case "4":
            return getRollingQuarterAverageFilters(calculationDate)
        case "7":
            return getThirdWednesdayOfMonthFilters(calculationDate, loadMaxDateData)
        case "8":
            return getFourthWednesdayOfMonthFilters(calculationDate, loadMaxDateData)
        case "9":
            return getMidMonthAverageFilters(calculationDate, 21, 20)
        case "10":
            return getMidMonthAverageFilters(calculationDate, 26, 25)
        default:
            return null
    }

}

def fallIntoSpecificDate(referencePeriod) {
    return (referencePeriod == "5" || referencePeriod == "6" || referencePeriod == "11" || referencePeriod == "7" || referencePeriod == "8")
}