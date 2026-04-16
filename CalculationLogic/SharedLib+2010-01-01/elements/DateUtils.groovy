import groovy.transform.Field
import net.pricefx.formulaengine.DatamartContext

@Field Map TIME_PERIOD_TYPES = [MONTHLY      : "Monthly",
                                QUARTERLY    : "Quarterly",
                                SEMI_ANNUALLY: "Semi-Annually",
                                ANNUALLY     : "Annually"]

/**
 * Returns the lesser of two provided dates
 * @param date1
 * @param date2
 * @return
 */
Date mindate(Date date1, Date date2) {
    return date1 < date2 ? date1 : date2
}

/**
 * Returns the greater of two provided dates
 * @param date1
 * @param date2
 * @return
 */
Date maxdate(Date date1, Date date2) {
    return date1 > date2 ? date1 : date2
}

/**
 * Returns the number of days of overlap (intersect) within two provided date periods
 * @param start1 Start date of first period
 * @param end1 End date of first period
 * @param start2 Start date of second period
 * @param end2 End date of second period
 * @return Integer number of days where the two periods intersect
 */
int intersectingDays(Date start1, Date end1, Date start2, Date end2) {
    return Math.max(mindate(end1, end2) - maxdate(start1, start2) + 1, 0)
}

/**
 * Returns the current year as integer
 * @return
 */
int currentYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
}

/**
 * Returns the number of days within specified month of the year
 * @param month Number of month in year - ie, Jan = 1, Dec = 12.
 * @param year The year of occurrence, because its not always the same every year. If not provided or given null, defaults to current year
 * @return Integer number of days in specified month
 */
int daysInMonth(int month, Integer year = null) {
    if (!year) year = currentYear()

    def cal = Calendar.getInstance()
    cal.set(year, (month - 1), 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

BigDecimal getDaysInYearToCurrentYearDaysRatio() {
    Calendar calendar = api.calendar()
    BigDecimal currentYearDay = calendar.get(Calendar.DAY_OF_YEAR) as BigDecimal
    BigDecimal daysInCurrentYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR) as BigDecimal

    return daysInCurrentYear / currentYearDay as BigDecimal
}

Calendar getCalendarFromDate(Date date) {
    Calendar calendar = getTimeFreeCalendar()
    calendar.setTime(date)

    return calendar
}

/**
 * Warning: 29 February + 1 years is 1st of March. Good luck waiting 4 years to reproduce bugs
 */
Calendar offsetCalendarFromToday(int daysOffset, int monthsOffset, int yearsOffset) {
    Calendar calendar = getTodaysDate()
    offsetCalendar(calendar, daysOffset, monthsOffset, yearsOffset)

    return calendar
}

Calendar offsetCalendar(Calendar calendar, int daysOffset, int monthsOffset, int yearsOffset) {
    calendar.add(Calendar.DATE, daysOffset)
    calendar.add(Calendar.MONTH, monthsOffset)
    calendar.add(Calendar.YEAR, yearsOffset)

    return calendar
}

Calendar getYearStartDate(int yearsOffsetFromCurrentYear = 0) {
    Calendar calendar = getCurrentYearStartDate()
    calendar.add(Calendar.YEAR, yearsOffsetFromCurrentYear)

    return calendar
}

Calendar getYearEndDate(int yearsOffsetFromCurrentYear = 0) {
    Calendar calendar = getCurrentYearEndDate()
    calendar.add(Calendar.YEAR, yearsOffsetFromCurrentYear)

    return calendar
}

Calendar getMonthEndDate(int monthsOffsetFromCurrentMonth = 0) {
    Calendar calendar = getMonthStartDate(monthsOffsetFromCurrentMonth)
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

    return calendar
}

Date getMonthEndDate(Date date) {
    Calendar calendar = getCalendarFromDate(date)
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

    return calendar.getTime()
}

Calendar getMonthStartDate(int monthsOffsetFromCurrentMonth = 0) {
    Calendar calendar = getCurrentMonthStartDate()
    calendar.add(Calendar.MONTH, monthsOffsetFromCurrentMonth)

    return calendar
}

Date getMonthStartDate(Date date) {
    Calendar calendar = getCalendarFromDate(date)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    return calendar.getTime()
}

/**
 * Week starts at monday, this is in line to how calendar returned from api.calendar() works
 */
Calendar getWeekStartDate(int weeksOffsetFromCurrentWeek = 0) {
    Calendar calendar = getCurrentWeekStartDate()
    calendar.add(Calendar.DATE, weeksOffsetFromCurrentWeek * 7)

    return calendar
}

Calendar getDayStartDate(int daysOffsetFromCurrentDay = 0) {
    Calendar calendar = getTodaysDate()
    calendar.add(Calendar.DATE, daysOffsetFromCurrentDay)

    return calendar
}

String getFormattedDate(Calendar calendar, String format = "yyyy-MM-dd") {
    return getFormattedDate(calendar.getTime(), format)
}

String getFormattedDate(Date date, String format = "yyyy-MM-dd") {
    return date.format(format)
}

Date parseToDate(String date, String format = "yyyy-MM-dd") {
    return Date.parse(format, date)
}

/**
 * If targetDate is available in current calculation context and is not null, it will be used instead of current time.
 * This is due to behavior of api.calendar() function.
 * @return
 */
Calendar getTodaysDate() {
    return getTimeFreeCalendar()
}

/**
 * Get list of time period
 * @param startDate - start date
 * @param endDate - end date
 * @param timePeriodType - one item of TIME_PERIOD_TYPES (MONTHLY, QUARTERLY, SEMI_ANNUALLY, ANNUALLY)
 * @return List of Map - Map structure  is [getStartDate : Closure<Date>,
 *                                          getEndDate   : Closure<Date>,
 *                                          getPeriodName: Closure<String>]
 */
List getTimePeriods(Date startDate, Date endDate, String timePeriodType = null) {
    if (!timePeriodType) {
        return [buildTimePeriodData(startDate, endDate, "")]
    }

    List calendarData = getCalendarData(startDate, endDate, timePeriodType)

    if (!calendarData) {
        return []
    }

    List timePeriods = []
    int counter = 0

    Map comparedData = calendarData.getAt(0)
    int endIndex = calendarData.size() - 1

    for (calendarDataRecord in calendarData) {
        if (calendarDataRecord.periodName != comparedData.periodName) {
            timePeriods.add(buildTimePeriodData(comparedData.calDate,
                    (calendarDataRecord.calDate - 1),
                    comparedData.periodName))

            comparedData = calendarDataRecord
        }

        if (counter == endIndex) {
            timePeriods.add(buildTimePeriodData(comparedData.calDate,
                    calendarDataRecord.calDate,
                    comparedData.periodName))
        }

        counter++
    }

    return timePeriods
}

protected Calendar getCurrentYearStartDate() {
    Calendar calendar = getTimeFreeCalendar()
    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR))

    return calendar
}

protected Calendar getCurrentYearEndDate() {
    Calendar calendar = getTimeFreeCalendar()
    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))

    return calendar
}

protected Calendar getCurrentMonthStartDate() {
    Calendar calendar = getTimeFreeCalendar()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))

    return calendar
}

protected Calendar getCurrentMonthEndDate() {
    Calendar calendar = getTimeFreeCalendar()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

    return calendar
}

protected Calendar getCurrentWeekStartDate() {
    Calendar calendar = getTimeFreeCalendar()
    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek())

    return calendar
}

/**
 * api.calendar always returns beginning of current date (time is 00:00:00) due to precision of targetDate.
 * To be extra sure, we manually clear time, to handle cases where not targetDate is specified
 *
 * If targetDate is available in current calculation context and is not null, it will be used instead of current time.
 */
protected Calendar getTimeFreeCalendar() {
    Calendar calendar = api.calendar()

    calendar.set(Calendar.HOUR, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar
}

/**
 * Get calendar data from data source 'cal'.
 * Datasource 'cal' doesn't support Semi-Annually field (Semi-Yearly)
 * We must build value for Semi-Annually with format 'YYYY-Sx' ('2020-S1', '2020-S2') with logic:
 *                                + if quarter in ('YYYY-Q1', 'YYYY-Q2') -> then Semi-Annually='YYYY-S1'
 *                                + if quarter in ('YYYY-Q3', 'YYYY-Q4') -> then Semi-Annually='YYYY-S2'
 * @param startDate - start date
 * @param endDate - end date
 * @param periodType - period type is defined in TIME_PERIOD_TYPES
 * @return list of Map - with map structure [calDate: 'YYYY-MM-dd', periodName: 'period name']
 *                       periodName have format based on periodType
 *                       MONTHLY       -> 'YYYY-Mxx' (e.g: 2020-M06, 2020-M07) //value & format as in data source 'cal'
 *                       QUARTERLY     -> 'YYYY-Qx' (e.g: 2020-Q2, 2020-Q3) //value & format as in data source 'cal'
 *                       SEMI_ANNUALLY -> 'YYYY-Mxx' (e.g: 2020-S1, 2020-S2)
 *                       ANNUALLY      -> 'YYYY' (e.g: 2020) //value &format as in data source 'cal'
 */
protected List getCalendarData(Date startDate, Date endDate, String periodType) {
    DatamartContext datamartContext = api.getDatamartContext()
    def datasource = datamartContext.getDataSource("cal")
    DatamartContext.Query query = datamartContext.newQuery(datasource, false)

    query.select("CalDate")
    query.select("CalMonth")
    query.select("CalQuarter")
    query.select("CalYear")

    query.where(Filter.greaterOrEqual("CalDate", startDate),
            Filter.lessOrEqual("CalDate", endDate))
    query.orderBy("CalDate")
    /*Create sql to get CalDate, CalMonth, CalQuarter, CalYear from datasource 'cal'
      Because the 'cal' datasource doesn't support Semi-Annually field (Semi-Yearly), we must build value for Semi-Annually from CalQuarter
      Format of CalQuarter value can be 'YYYY-Q1' or 'YYYY-Q2' or 'YYYY-Q3' or 'YYYY-Q4'
      If the last character of CalQuarter < 3 ('1' or '2') then Semi-Annually = 'YYYY-S1'
      If the last character of CalQuarter >= 3 ('3' or '4') then Semi-Annually = 'YYYY-S2'
     */
    String sql = """  SELECT CalDate            AS 'CalDate',
                             CalMonth           AS '${TIME_PERIOD_TYPES.MONTHLY}',
                             CalQuarter         AS '${TIME_PERIOD_TYPES.QUARTERLY}',
                             IIF( Substring(CalQuarter,7,1) < '3',
                                CalYear || '-S1',
                                CalYear || '-S2'
                               )                AS '${TIME_PERIOD_TYPES.SEMI_ANNUALLY}',
                             CalYear            AS '${TIME_PERIOD_TYPES.ANNUALLY}'
                    FROM T1
                    ORDER BY CalDate"""
    List calendarData = datamartContext.executeSqlQuery(sql, query)?.collect()

    return calendarData?.collect { Map data ->
        return [calDate   : data.CalDate,
                periodName: data.getAt(periodType)]
    }
}

protected Map buildTimePeriodData(Date startDate, Date endDate, String periodName) {
    return [getStartDate : { return startDate },
            getEndDate   : { return endDate },
            getPeriodName: { return periodName }]
}
