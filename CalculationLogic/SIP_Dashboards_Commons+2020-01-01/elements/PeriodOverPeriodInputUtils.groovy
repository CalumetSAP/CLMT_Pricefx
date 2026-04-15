import org.joda.time.DateTime

/**
 * Takes in a map of configuration settings for a measure, as well as a map of SQL configuration settings,
 * then parses the configuration settings to determine the type of measure, the aggregation type, the column to be used, and whether or not the measure is a ratio.
 *
 * If the measure is a single measure, the method creates a map containing the label, expression, and columns for that measure.
 * If the measure is a ratio measure, the method creates a map containing the label, expression, columns, and matrixLabel for that measure.
 *
 * Example:
 *      Inputs:
 *              configurator = [MEASURE_TYPE: [NAME: 'single'],
 *                MEASURE_AGGREGATION: [NAME: 'sum'],
 *                MEASURE_COLUMN: [NAME: 'revenue'],
 *                RATIO_TYPE: [NAME: null],
 *                RATIO_NUMERATOR: [NAME: null],
 *                RATIO_DENOMINATOR: [NAME: null]]
 *      Output:
 *               [label     : "Revenue",
 *                expression: "SUM(revenue)",
 *                columns   : [(revenue): "SUM(revenue) as revenue"]
 *
 * @param configurator Map a map of configuration settings for a measure
 * @param sqlConfiguration Map a map of SQL configuration settings
 * @return Map Parsed measure expression
 */
Map parseMeasureExpressionFromConfigurator(Map configurator, Map sqlConfiguration) {
    Map measureTypes = libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_CONFIGURATOR_CONFIG.MEASURE_TYPES
    Map inputConfigs = libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_CONFIGURATOR_CONFIG.INPUTS

    String measureType = configurator.getAt(inputConfigs.MEASURE_TYPE.NAME)
    String measureAggregation = libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_MEASURE_AGGREGATION
    String measureColumn = configurator.getAt(inputConfigs.MEASURE_COLUMN.NAME)
    String ratioType = configurator.getAt(inputConfigs.RATIO_TYPE.NAME)
    String ratioNumerator = configurator.getAt(inputConfigs.RATIO_NUMERATOR.NAME)
    String ratioDenominator = configurator.getAt(inputConfigs.RATIO_DENOMINATOR.NAME)
    Map dmColumnOptionLabels = libs.SIP_Dashboards_Commons.InputUtils.getDatamartNumericColumnOptions(sqlConfiguration.datamartName)

    switch (measureType) {
        case measureTypes.SINGLE:
            return [
                    label     : dmColumnOptionLabels[measureColumn],
                    expression: "$measureAggregation($measureColumn)",
                    columns   : [(measureColumn): "$measureAggregation($measureColumn) as $measureColumn"]
            ]
        case measureTypes.RATIO:
            String expression = ratioType == libs.SIP_Dashboards_Commons.ConstConfig.RATIO_TYPES.PERCENTAGE_BASED.PRICE_LEAKAGE
                    ? "SUM($ratioDenominator - $ratioNumerator) / SUM($ratioDenominator)"
                    : "SUM($ratioNumerator) / SUM($ratioDenominator)"

            String numeratorSumColumnLabel = dmColumnOptionLabels[ratioNumerator]
            String denominatorSumColumnLabel = dmColumnOptionLabels[ratioDenominator]

            return [
                    label      : ratioType,
                    expression : expression,
                    columns    : [(ratioNumerator)  : "SUM($ratioNumerator) as $ratioNumerator",
                                  (ratioDenominator): "SUM($ratioDenominator) as $ratioDenominator"],
                    matrixLabel: [(ratioNumerator.toLowerCase())  : numeratorSumColumnLabel,
                                  (ratioDenominator.toLowerCase()): denominatorSumColumnLabel]
            ]
        default:
            api.throwException("Unhandled Measure Type $measureType")
    }
}

/**
 * Parse a period string into a DateTime object
 * The period string represents a period in time and can be of different granularities (year, quarter, month, quad week, week, day)
 * Example:
 *      Input: 2021-Q1
 *      Output: 2021-04-01
 * @param periodString String period string, only accept in format like 2022-W26 or 2019-Q4 etc.
 * @return DateTime Parsed period in DateTime object
 */
DateTime parseFromPeriodString(String periodString) {
    if (!validatePeriodString(periodString)) {
        api.throwException "Unable to parse date from period string $periodString"
    }

    String periodDelimiter = libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_CONFIGURATOR_CONFIG.PERIOD_DELIMITER
    List<String> subStrings = periodString.tokenize(periodDelimiter)
    int year = Integer.parseInt(subStrings[0])
    DateTime dt = getCurrentTimeWithAlteredYear(year)
    DateTime.Property result = null

    // if there is a periodic component more granular than year
    if (subStrings.size() == 1) {
        result = dt.yearOfCentury()
    } else {
        Map periodTypes = libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_PERIOD_TYPES
        String periodType = extractPeriodType(periodString)
        Integer periodSize = extractPeriodSize(periodString)

        switch (periodType) {
            case periodTypes.DAY.CODE:
                result = dt.withDayOfYear(periodSize).dayOfYear()
                break
            case periodTypes.WEEK.CODE:
                result = dt.withWeekOfWeekyear(periodSize).weekOfWeekyear()
                break
            case periodTypes.QUAD_WEEK.CODE:
                result = dt.withWeekOfWeekyear(periodSize * 4).weekOfWeekyear()
                break
            case periodTypes.MONTH.CODE:
                result = dt.withMonthOfYear(periodSize).monthOfYear()
                break
            case periodTypes.QUARTER.CODE:
                result = dt.withMonthOfYear(periodSize * 3).monthOfYear()
                break
            case periodTypes.YEAR.CODE:
                result = dt.withYearOfCentury(periodSize).yearOfCentury()
                break
        }
    }

    return result.roundCeilingCopy()
}

/**
 * Get period type that is D or M or W or QW or Q or Y
 * @param periodString string in format 2022-W26 or 2019-Q4 etc
 * @return the period type
 */
String extractPeriodType(String periodString) {
    return (periodString =~ /[DMWQY]+/)?.getAt(0)
}

/**
 * Get period size
 * @param periodString string in format 2022-W26 or 2019-Q4 etc
 * @return the period size
 */
Integer extractPeriodSize(String periodString) {
    return (periodString =~ /[A-Z](\d+)/).getAt(0)?.getAt(1) as Integer
}

/***
 * Check if the string is in format 2022-W26 or 2019-Q4 etc
 * @param periodString
 * @return
 */
boolean validatePeriodString(String periodString) {
    return periodString
            ? periodString.matches("^\\d{4}-((D\\d{1,3})|(Q\\d{1})|([WM]\\d{1,2})|(QW\\d{1,2}))\$|^Y?\\d{4}\$")
            : false
}

/**
 * Returns the start date of a period defined by the parameters.
 * Starting from "startPoint" based on the defined "intervalSize" we subtract the amount of periods equal to "numberOfIntervals"
 * Example:
 *  Inputs:
 *      - startPoint:  2022-03-01
 *      - intervalSize: Month
 *      - numberOfIntervals: 24
 *  Output:
 *      - result: 2020-03-01
 *
 * A special case for when Interval Size = "Quarter":
 * For example, with the start point is "2022-06-30" which is in the 2022-Q2, the first date of 2022-Q2 is 2022-04-01
 *
 * @param startPoint DateTime The start point
 * @param intervalSize String the interval size (Year, Quarter, Month, QuadWeek, Week, Day)
 * @param numberOfIntervals Integer The number of intervals
 * @return DateTime the derive period start
 */
DateTime derivePeriodStart(DateTime startPoint, String intervalSize, Integer numberOfIntervals) {
    DateTime result = null
    Map periodTypes =  libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_PERIOD_TYPES

    switch (intervalSize) {
        case periodTypes.DAY.TEXT:
            result = startPoint.minusDays(numberOfIntervals)
            break
        case periodTypes.WEEK.TEXT:
            result = startPoint.minusWeeks(numberOfIntervals)
            break
        case periodTypes.QUAD_WEEK.TEXT:
            result = startPoint.minusWeeks(numberOfIntervals * 4)
            break
        case periodTypes.MONTH.TEXT:
            result = startPoint.minusMonths(numberOfIntervals)
            break
        case periodTypes.QUARTER.TEXT:
            // Set the month and day of the  to the first date of the start quarter
            result = startPoint
                    .minusMonths((numberOfIntervals - 1) * 3)
                    .minusMonths(2) // Since the startPoint's month always the last month of quarter, so we need to minus by 2 to get the first month of quarter
                    .withDayOfMonth(1) // get the first date
            break
        case periodTypes.YEAR.TEXT:
            result = startPoint.minusYears(numberOfIntervals)
            break
        default:
            api.throwException("Unhandled case deriving $intervalSize from $startPoint")
    }

    return result
}

/**
 * Returns the end date of a period defined by the parameters.
 * Starting from "startPoint" based on the defined "intervalSize" we subtract the amount of periods equal to "numberOfIntervals"
 * Example:
 *  Inputs:
 *      - startPoint:  2022-03-01
 *      - intervalSize: Month
 *      - numberOfIntervals: 24
 *  Output:
 *      - result: 2020-03-01
 * @param startPoint DateTime The start point
 * @param intervalSize String the interval size (Year, Quarter, Month, QuadWeek, Week, Day)
 * @param numberOfIntervals Integer The number of intervals
 * @return DateTime the derive period start
 */
DateTime derivePeriodEnd(DateTime startPoint, String intervalSize, Integer numberOfIntervals) {
    DateTime result = null
    Map periodTypes =  libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_PERIOD_TYPES

    switch (intervalSize) {
        case periodTypes.DAY.TEXT:
            result = startPoint.minusDays(numberOfIntervals)
            break
        case periodTypes.WEEK.TEXT:
            result = startPoint.minusWeeks(numberOfIntervals)
            break
        case periodTypes.QUAD_WEEK.TEXT:
            result = startPoint.minusWeeks(numberOfIntervals * 4)
            break
        case periodTypes.MONTH.TEXT:
            result = startPoint.minusMonths(numberOfIntervals)
            break
        case periodTypes.QUARTER.TEXT:
            result = startPoint.minusMonths(numberOfIntervals * 3)
            break
        case periodTypes.YEAR.TEXT:
            result = startPoint.minusYears(numberOfIntervals)
            break
        default:
            api.throwException("Unhandled case deriving $intervalSize from $startPoint")
    }

    return result
}

/**
 * Build the list that contains period definition from the input number of intervals
 * Depend on the interval size, it's will get the corresponding period and update the start point accordingly
 * Example 1:
 *      Inputs:
 *          - startPoint: 2020-01-01
 *          - intervalSize: Year
 *          - numberOfIntervals: 2
 *      Output:
 *          - Result: [2020, 2021]
 *
 * Example 2:
 *      Inputs:
 *          - startPoint: 2021-10-01
 *          - intervalSize: Month
 *          - numberOfIntervals: 5
 *      Output:
 *          - Result: [2021-M10, 2021-M11, 2021-M12, 2022-M01, 2022-M02]
 * @param startPoint DateTime start date
 * @param intervalSize String interval size (Year, Quarter, Month, QuadWeek, Week, Day)
 * @param numberOfIntervals Integer number of intervals
 * @return List the period definition list
 */
List<String> enumerateIntervals(DateTime startPoint, String intervalSize, Integer numberOfIntervals) {
    List<String> uniqueIntervals = []
    for (int i = 0; i < numberOfIntervals; i++) {
        String period = startPoint.getYear()
        Map intervalTypes = libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_PERIOD_TYPES

        switch (intervalSize) {
            case intervalTypes.DAY.TEXT:
                period += "-D" + String.format("%03d", startPoint.getDayOfYear())
                startPoint = startPoint.plusDays(1)
                break
            case intervalTypes.WEEK.TEXT:
                period += "-W" + String.format("%02d", startPoint.getWeekOfWeekyear())
                startPoint = startPoint.plusWeeks(1)
                break
            case intervalTypes.QUAD_WEEK.TEXT:
                BigDecimal quadWeekNum = Math.floor(((startPoint.getWeekOfWeekyear() - 1) / 4)) + 1
                period += "-QW" + String.format("%02d", quadWeekNum.intValue())
                startPoint = startPoint.plusWeeks(4)
                break
            case intervalTypes.MONTH.TEXT:
                period += "-M" + String.format("%02d", startPoint.getMonthOfYear())
                startPoint = startPoint.plusMonths(1)
                break
            case intervalTypes.QUARTER.TEXT:
                period += "-Q" + (Math.ceil(startPoint.getMonthOfYear() / 3) as int)
                startPoint = startPoint.plusMonths(3)
                break
            case intervalTypes.YEAR.TEXT:
                startPoint = startPoint.plusYears(1)
        }

        uniqueIntervals << period
    }

    return uniqueIntervals
}

/**
 * Check if the ratio type is percentage based or not
 * @param ratioType String The ratio type
 * @return boolean result
 */
boolean isRatioPercent(String ratioType) {
    return libs.SIP_Dashboards_Commons.ConstConfig.RATIO_TYPES.PERCENTAGE_BASED.values().contains(ratioType)
}

/**
 * Check if the ratio type is absolute based or not
 * @param ratioType String The ratio type
 * @return boolean result
 */
boolean isRatioAbsolute(String ratioType) {
    return libs.SIP_Dashboards_Commons.ConstConfig.RATIO_TYPES.ABSOLUTE_BASED.values().contains(ratioType)
}


/**
 * Check if the measure type is RATIO or not
 * @param measureType String The measure type
 * @return boolean result
 */
boolean isRatioType(String measureType) {
    def configs = libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_CONFIGURATOR_CONFIG

    return measureType == configs.MEASURE_TYPES.RATIO
}

/**
 * Get the current time for the year defined as parameter
 * @param year int input year
 * @return DateTime result
 */
DateTime getCurrentTimeWithAlteredYear(int year) {
    return DateTime.newInstance().withYear(year)
}
