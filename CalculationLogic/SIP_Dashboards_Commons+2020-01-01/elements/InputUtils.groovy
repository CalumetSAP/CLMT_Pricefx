import groovy.transform.Field
import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ConfiguratorEntry
import net.pricefx.server.dto.calculation.ContextParameter

@Field final String CACHE_SCOPE = "DB_PeriodicComparison"

Map getDatamartNumericColumnOptions(String dataMartName) {
    return libs.SharedLib.CacheUtils.getOrSet("$CACHE_SCOPE-Numeric-DatamartColumnOpts-$dataMartName") {
        Map dm = api.find("DM", Filter.equal("uniqueName", dataMartName))?.getAt(0)

        return ((List) dm.fields).findAll { Map field -> field.numeric == true }.collectEntries { Map field ->
            [(field.name): field.label]
        }.sort { Map.Entry field1, Map.Entry field2 -> field1.value <=> field2.value }
    }
}

/**
 * Retrieves the datamart filter user entry, used to generate GeneralFilters input on the dashboards.
 * User default values are applied if applicable.
 * @param datamartName datamart that is the source for the datamartFilterBuilderUserEntry
 * @return value of Filter retrieved from datamartFilterBuilderUserEntry
 */
Filter getDatamartFilterUserEntry(String datamartName) {
    Map inputConfig = libs.SIP_Dashboards_Commons.ConstConfig.DEFAULT_CONFIGURATOR_CONFIG.INPUTS.GENERAL_FILTER
    String userEntryLabel = inputConfig.LABEL
    def userEntry = api.datamartFilterBuilderUserEntry(userEntryLabel, datamartName)

    return applyInputDefaultValue(userEntry, userEntryLabel, null)
}

/**
 * Creates the product group entry input.
 * @param inputName name of the input
 * @param defaultValue the default value, usually userDefault value coming from the default filters.
 * @return value of the user input
 */
def getProductGroupUserEntry(String inputName, Map defaultValue) {
    def productEntry = api.datamartProductGroupEntry(inputName)

    return applyInputDefaultValue(productEntry, inputName, defaultValue, true)
}

/**
 * Creates the customer group entry input.
 * @param inputName name of the input
 * @param defaultValue the default value, usually userDefault value coming from the default filters.
 * @return value of the user input
 */
def getCustomerGroupUserEntry(String inputName, Map defaultValue) {
    def customerEntry = api.datamartCustomerGroupEntry(inputName)

    return applyInputDefaultValue(customerEntry, inputName, defaultValue, true)
}

/**
 * Creates the date from user entry input.
 * The value of a default date from entry is always year back.
 * @param inputName name of the input
 * @param allowNulls defines whether the input considers null values as a valid input
 * @return value of the user input
 */
def getDefaultDateFromUserEntry(String inputName, boolean allowNulls = false) {
    java.util.Calendar calendar = java.util.Calendar.getInstance()
    calendar.add(java.util.Calendar.YEAR, -1)
    def defaultValue = calendar.getTime()

    return getDateUserEntry(inputName, defaultValue, allowNulls)
}

/**
 * Creates the date to user entry input.
 * The value of a default date from entry is always current year.
 * @param inputName name of the input
 * @param allowNulls defines whether the input considers null values as a valid input
 * @return value of the user input
 */
def getDefaultDateToUserEntry(String inputName, boolean allowNulls = false) {
    def defaultValue = java.util.Calendar.getInstance().getTime()

    return getDateUserEntry(inputName, defaultValue, allowNulls)
}

/**
 * Creates the date user entry input.
 * @param inputName name of the input
 * @param defaultValue the default value, usually userDefault value coming from the default filters.
 * @param allowNulls defines whether the input considers null values as a valid input
 * @return value of the user input
 */
def getDateUserEntry(String inputName, def defaultValue, boolean allowNulls = false) {
    def dateEntry = api.dateUserEntry(inputName)

    return applyInputDefaultValue(dateEntry, inputName, defaultValue, allowNulls)
}

/**
 * Creates the options user entry input.
 * @param inputName name of the input
 * @param values values available for selection in the input
 * @param defaultValue the default value, usually userDefault value coming from the default filters.
 * @param allowNulls defines whether the input considers null values as a valid input
 * @return value of the user input
 */
def getOptionsUserEntry(String inputName, List values, String defaultValue, boolean allowNulls = false) {
    def optionsEntry = api.option(inputName, values)

    return applyInputDefaultValue(optionsEntry, inputName, defaultValue, allowNulls)
}

/**
 * Creates the options user entry input.
 * @param inputName name of the input
 * @param values values available for selection in the input, these are the values returned
 * @param labels labels for the values, Map of structure [value : label]
 * @param defaultValue the default value, usually userDefault value coming from the default filters.
 * @param allowNulls defines whether the input considers null values as a valid input
 * @return value of the user input
 */
def getOptionsUserEntry(String inputName, List values, Map labels, String defaultValue, boolean allowNulls = false) {
    def optionsEntry = api.option(inputName, values, labels)

    return applyInputDefaultValue(optionsEntry, inputName, defaultValue, allowNulls)
}

/**
 * Creates the boolean user entry input.
 * @param inputName name of the input
 * @param defaultValue the default value, usually userDefault value coming from the default filters.
 * @param allowNulls defines whether the input considers null values as a valid input
 * @return value of the user input
 */
def getBooleanUserEntry(String inputName, boolean defaultValue, boolean allowNulls = false) {
    def booleanEntry = api.booleanUserEntry(inputName)

    return applyInputDefaultValue(booleanEntry, inputName, defaultValue, allowNulls)
}

/**
 * Applies the default value for a given user input.
 * User default value always has priority.
 * @param userEntry Object defining the processed user entry
 * @param inputName name of the input
 * @param defaultValue the default value, usually userDefault value coming from the default filters.
 * @param allowNulls defines whether the input considers null values as a valid input
 * @return Object defining the processed user entry with default value
 */
def applyInputDefaultValue(def userEntry, String inputName, def defaultValue, boolean allowNulls = false) {
    def param = api.getParameter(inputName)

    if (param != null && param?.getValue() == null) {
        param.setValue(defaultValue)
    }

    return allowNulls ?
            userEntry :
            (userEntry ?: defaultValue)
}

/**
 * Retrieves the current quarter in DM friendly format.
 * @return current quarter in a format that's generated by system PricingDate function
 */
String getCurrentQuarter() {
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH)

    return getFormattedPeriod((currentMonth / 3 as int) + 1, "Q")
}

/**
 * Retrieves the current week in DM friendly format.
 * @return current week in a format that's generated by system PricingDate function
 */
String getCurrentWeek() {
    int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)

    return getFormattedPeriod(currentWeek, "W")
}

/**
 * Retrieves the current month in DM friendly format.
 * @return current month in a format that's generated by system PricingDate function
 */
String getCurrentMonth() {
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    return getFormattedPeriod(currentMonth, "M")
}

/**
 * Get formatted period string.
 * @param period the time value
 * @param prefix can be [Q, W, M].
 * When it's Q (any quarter), the period value is the current quarter.
 * @return
 */
String getFormattedPeriod(int period, String prefix) {
    String result

    switch (prefix) {
        case "Q":
            result = "Q" + period
            break
        case "W":
        case "M":
            result = period < 10 ? prefix + "0" + period : prefix + period
    }

    return result
}

/**
 * Generates date type input entry.
 * @param inputConfig config for a given input. The configs can be found in libs.SIP_Dashboards_Commons.ConstConfig element.
 * @return ConfiguratorEntry of type date to be used in the configurator.
 */
ConfiguratorEntry getDateUserInput(Map inputConfig) {
    Date defaultDate = getDefaultDateForFirstInput(inputConfig)

    ConfiguratorEntry configurator = api.createConfiguratorEntry()
    ContextParameter parameter = configurator.createParameter(InputType.DATEUSERENTRY, inputConfig.UNIQUE_KEY)
            .setLabel(inputConfig.LABEL)

    libs.SIP_Dashboards_Commons.ConfiguratorUtils.setParameterDefaultValue(parameter, defaultDate)
    libs.SIP_Dashboards_Commons.ConfiguratorUtils.setNoRefreshParameter(configurator)

    return configurator
}

/**
 * Gets the default fallback date. This is to prevent selecting the whole data set.
 * By default the default fallback date is last year for DateTo and the year before the last year for DateFrom.
 * @param inputConfig config for a given input. The configs can be found in libs.SIP_Dashboards_Commons.ConstConfig element.
 */
Date getDefaultDateForFirstInput(Map inputConfig) {
    Map defaultDateSetup = inputConfig.DEFAULT_VALUE
    Calendar defaultDate = Calendar.getInstance()
    defaultDate.add(Calendar.YEAR, defaultDateSetup.YEAR_ADJUSTMENT) //TODO updated it with PFPCS-5611

    Integer month = defaultDateSetup.MONTH

    if (month != null) {
        defaultDate.set(Calendar.MONTH, month)
    }

    Integer day = defaultDateSetup.DAY

    if (month != null) {
        defaultDate.set(Calendar.DAY_OF_MONTH, day)
    }

    return defaultDate.getTime()
}

/**
 * Retrieves the selected value from the options, in case of no selected value a default one is set.
 * Used in aggregation inputs.
 * @param optionLabel name of the options user entry
 * @param dimensions list of the values displayed in the entry
 * @param selected currently selected user value
 * @return default or selected value depending on the data availability
 */
protected def getSelectedOrDefaultValue(String optionLabel, Map<String, String> dimensions, def selected) {
    def param = api.getParameter(optionLabel)

    if (param == null || param.getValue() != null) {
        return selected
    }

    String firstDimension = dimensions.keySet().getAt(0)
    param.setValue(firstDimension)

    return firstDimension
}

/**
 * Creates the aggregation input that's used in various dashboards to provide additional level of data aggregation.
 * Contains list of all defined dimensions in the sqlConfiguration
 * @param dimensions list of the values displayed in the entry
 * @param optionLabel label of the entry
 * @return value of the aggregation input
 */
protected def createAggregationInput(Map dimensions, String optionLabel) {
    def selectedByUser = api.option(optionLabel, dimensions.keySet() as List, dimensions)

    return getSelectedOrDefaultValue(optionLabel, dimensions, selectedByUser)
}
