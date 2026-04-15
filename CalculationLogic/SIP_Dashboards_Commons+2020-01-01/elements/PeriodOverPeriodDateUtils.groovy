import org.joda.time.DateTime

/**
 * Get current time
 * @return DateTime current time
 */
DateTime getCurrentTime() {
    return DateTime.now()
}

/**
 * Calculates the end date of the base period based on the interval size and final interval type.
 * @param configurator Map The dashboard configurator
 * @param sqlConfiguration Map the sql configuration
 * @return DateTime the base period end
 */
DateTime getBasePeriodEnd(Map sqlConfiguration, String intervalSize, String finalInterval, String finalIntervalX) {
    def commonConfigs = libs.SIP_Dashboards_Commons.ConstConfig

    Map intervalTypes = commonConfigs.PERIOD_OVER_PERIOD_FINAL_INTERVAL_OPTIONS
    Map periodTypes = commonConfigs.PERIOD_OVER_PERIOD_PERIOD_TYPES

    Integer minusIntervalSize
    DateTime now = getCurrentTime()
    DateTime basePeriodEndValue = null

    if (finalInterval == intervalTypes.MANUAL) {
        basePeriodEndValue = libs.SIP_Dashboards_Commons.PeriodOverPeriodInputUtils.parseFromPeriodString(finalIntervalX)
    } else if ([intervalTypes.X_AGO, intervalTypes.LATEST].contains(finalInterval)) {
        // exclude the current period
        minusIntervalSize = (finalIntervalX ? finalIntervalX as int : 0) + 1
        DateTime yesterday = now.minusDays(1) // To match with lessOrEqual query filter

        switch (intervalSize) {
            case periodTypes.DAY.TEXT:
                basePeriodEndValue = now.minusDays(minusIntervalSize)
                        .dayOfYear()
                        .roundCeilingCopy()
                break
            case periodTypes.WEEK.TEXT:
                basePeriodEndValue = yesterday
                        .minusWeeks(minusIntervalSize)
                        .weekOfWeekyear()
                        .roundCeilingCopy()
                basePeriodEndValue = getWeekEndDate(sqlConfiguration, basePeriodEndValue)
                break
            case periodTypes.QUAD_WEEK.TEXT:
                basePeriodEndValue = yesterday
                        .minusWeeks(yesterday.getWeekOfWeekyear() % 4)
                        .weekOfWeekyear()
                        .roundCeilingCopy()
                basePeriodEndValue = getWeekEndDate(sqlConfiguration, basePeriodEndValue)
                break
            case periodTypes.MONTH.TEXT:
                basePeriodEndValue = yesterday
                        .minusMonths(minusIntervalSize)
                        .monthOfYear()
                        .roundCeilingCopy()
                break
            case periodTypes.QUARTER.TEXT:
                // Get the previous quarter end month
                int previousQuarterEndMonth = (yesterday.getMonthOfYear() / 3) * 3

                // Since the default value of minusIntervalSize is 1, and we're already getting the previous quarter,
                // so we need to subtract minusIntervalSize by 1 here.
                int minusIntervalMonth = (minusIntervalSize - 1) * 3

                basePeriodEndValue = yesterday
                        .withMonthOfYear(previousQuarterEndMonth)
                        .minusMonths(minusIntervalMonth)
                        .plusMonths(1) // get the first month of next quarter
                        .withDayOfMonth(1) // Get the first date of next quarter
                        .minusDays(1) // minus 1 to get the previous quarter's end date
                break
            case periodTypes.YEAR.TEXT:
                basePeriodEndValue = yesterday
                        .minusYears(minusIntervalSize)
                        .yearOfCentury()
                        .roundCeilingCopy()
        }
    }

    return basePeriodEndValue
}

/**
 * Get end of the week date
 * @param sqlConfiguration map The  SQL configuration (from configurator)
 * @param now DateTime The current datetime
 * @return DateTime The date indicating the end of the week
 */
DateTime getWeekEndDate(Map sqlConfiguration, DateTime now) {
    Calendar calendar = libs.SharedLib.DateUtils.getCalendarFromDate(now.toDate())
    calendar.setFirstDayOfWeek(sqlConfiguration.firstDayOfWeek == libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_DAY_OF_WEEK_MONDAY ? Calendar.MONDAY : Calendar.SUNDAY)

    while (calendar.get(Calendar.DAY_OF_WEEK) != calendar.getFirstDayOfWeek()) {
        calendar.add(Calendar.DATE, 1)
    }

    return new DateTime(calendar)
}


/**
 * Check if the interval size is Day
 * @return boolean is Day interval size or not
 */
protected boolean isDayIntervalSize(String intervalSize) {
    Map intervalOptionsConfig = libs.SIP_Dashboards_Commons.ConstConfig.PERIOD_OVER_PERIOD_CONFIGURATOR_CONFIG.INTERVAL_OPTIONS

    return intervalOptionsConfig.DAY.VALUE.equals(intervalSize)
}
