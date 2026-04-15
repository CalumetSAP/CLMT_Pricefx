/**
 * Retrieves the main currency code for a given datamart.
 * @param datamartName name of the datamart
 * @return currency code that is defined for a given datamart
 */
String getDatamartCurrencyCode(String datamartName) {
    return api.find("DM", Filter.equal("uniqueName", datamartName))?.getAt(0)?.baseCcyCode
}

/**
 * Retrieves the currency symbol for the provided currency code.
 * The mapping between currency codes and currency symbols is defined in CurrencySymbols PP table.
 * In case of no currency code the "" is returned
 * @param currencyCode currency code of the selected currency in UserCurrency. Used for currency conversions.
 * @return currency symbol for the provided currency code.
 */
String getCurrencySymbol(String currencyCode) {
    return currencyCode ? api.vLookup(libs.SIP_Dashboards_Commons.ConstConfig.CURRENCY_SYMBOL_PP_NAME, currencyCode) : ""
}

//TODO: Add warning about missing currency symbol, when error handling will be done https://pricefx.atlassian.net/browse/PFPCS-1663
/**
 * Retrieves the format with currency symbol appended.
 * @param format format to be used, usually retrieved from libs.HighchartsLibrary.ConstConfig.TOOLTIP_POINT_Y_FORMAT_TYPES
 * @param currencySymbol currency symbol to be appended
 * @return format with currency symbol
 */
String getFormatWithCurrencySymbol(String format, String currencySymbol) {
    return "${format} ${currencySymbol}"
}

/**
 * Retrieves the currency data for a provided currencyCode.
 * Used to standardize the output of the TargetCurrencyData elements in the dashboards.
 * @param currencyCode currency code of the selected currency in UserCurrency. Used for currency conversions.
 * @return map containing the currencyCode and the currencySymbol for a given currency code.
 */
Map getCurrencyData(String currencyCode) {
    return [currencyCode  : currencyCode,
            currencySymbol: getCurrencySymbol(currencyCode)]
}

/**
 * Retrieves the list of all available currencies based on the provided ccyDSName currency data source.
 * Only the currencies that are valid and that have conversion defined from the given datamart currency are fetched.
 * The DS needs to have 1-1 conversion defined as well.
 * @param datamartName name of the datamart that will be used as a base for the initial currency
 * @return List of all currencies that have conversions defined based on the provided datamart base currency
 */
List<String> getAvailableCurrencies(String datamartName) {
    String fromCurrencyCode = getDatamartCurrencyCode(datamartName)
    Map commonsConfiguration = libs.SIP_Dashboards_Commons.ConfigurationUtils.getCommonsAdvancedConfiguration()

    def currentDate = Calendar.getInstance().time
    Map currencyQuery = [rollup      : true,
                         datamartName: commonsConfiguration.ccyDSName,
                         fields      : ["currencyTo": "CcyTo"],
                         whereFilters: [Filter.equal("CcyFrom", fromCurrencyCode),
                                        Filter.lessThan("CcyValidFrom", currentDate),
                                        Filter.greaterOrEqual("CcyValidTo", currentDate)]]

    return libs.HighchartsLibrary.QueryModule.queryDatasource(currencyQuery).collect { it.currencyTo }
            .sort()
}

/**
 * Retrieves the currency options selection user input.
 * The input is rendered only if there is more than one available currency, otherwise the base currency is returned.
 * @param datamartName name of the datamart that will be used as a base for the initial currency
 * @param inputConfig config for a given input. The configs can be found in libs.SIP_Dashboards_Commons.ConstConfig element.
 * @return currency code selected by the user, or default
 */
def getCurrencyOptions(String datamartName, Map inputConfig) {
    List<String> availableCurrencies = getAvailableCurrencies(datamartName)

    if (availableCurrencies.size() > 1) {
        String fromCurrencyCode = getDatamartCurrencyCode(datamartName)

        return libs.SIP_Dashboards_Commons.InputUtils.getOptionsUserEntry(inputConfig.LABEL,
                availableCurrencies,
                fromCurrencyCode)
    }

    return availableCurrencies.getAt(0)
}