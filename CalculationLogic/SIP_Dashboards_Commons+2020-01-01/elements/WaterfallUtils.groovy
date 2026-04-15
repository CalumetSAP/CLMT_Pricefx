import net.pricefx.domain.CustomerGroup
import net.pricefx.domain.ProductGroup

/**
 * Retrieves the Waterfall field definition structure from the "waterfall-configuration" deployed by Platform Manager during package installation.
 * The structure of the "waterfall-configuration" is strictly defined.
 * The fields that are disabled are omitted.
 * Each field has this structure:
 * ["name": null, <- name of the DM field for the data fetch for the element. Null for entries with sub elements and sums.
 * "label": "Local adjustments", <- label to be displayed to the user
 * "isSum": false, <- defines whether the value of the element should be calculated from all the previous elements
 * "isPercentBase": false, <- defines whether the value is a base for the percentage calculation used in the dashboard
 * "isSubtract": true, <- defines whether the value of the element should be reversed.
 * "disabled": false, <- defines whether the element is in use
 * "subLevel": <- defines the sub level structure, used in drilldown functionality. Value of parent element is equal to value of children
 *            [["name": "ForeignExchangeAdjustment", <- name of the DM field for the data fetch for the element.
 *             "label": "Foreign Exchange Adjustment", <- label to be displayed to the user
 *             "disabled": false, <- defines whether the value of the element should be reversed.
 *             "isSubtract": false], <- defines whether the value of the element should be reversed.
 *             ["name": "LocalAdjustment",
 *              "label": "Local Adjustment",
 *              "disabled": false,
 *              "isSubtract": false]]
 *  ]
 *
 * @return a structure of the waterfall fields used in the Waterfall Dashboards.
 */
List getWaterfallFieldsDefinition() {
    List waterfallFields = libs.SIP_Dashboards_Commons.ConfigurationUtils.getAdvancedConfiguration([:], "waterfall-configuration").getAt("waterfall-configuration").fields

    return waterfallFields.inject([]) { List result, Map field ->
        if (field.disabled) {
            return result
        }

        boolean containsSubElements = field.subLevel
        field.subLevel = field.subLevel?.findAll { !it.disabled }
        if (containsSubElements && !field.subLevel) {
            return result
        }

        result << field

        return result
    }
}

/**
 * Returns chart data for a given calculation model. The data is in Highchart compatible format.
 * @param sqlConfiguration sql configuration containing data mart fields mapping definition. Stored in SIP_AdvancedConfiguration.
 * @param dataForChart processed data for the chart returned from the processWaterfallFieldData.
 * @param rawQueryData raw query data returned from getWaterfallQueryResult
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param selectedModel selected calculation model, the list of available models can be found in the SIP_Dashboards_Commons.ConstConfig
 * @param colors a map of colors used for the series
 * @param drilldownName name of the drilldown, used as a suffix for the series to differentiate.
 * @return data for a given calculation model in a Highchart compatible format.
 * The structure is as follows:
 * [first : [mainSeriesData: [[name     : "Global List Price",
 *                             y        : 10,
 *                             color    : "#BF4040"],
 *                            [name     : "Local Adjustments",
 *                             y        : 20,
 *                             color    : "#BF4040",
 *                             drilldown: "LocalAdjustmentsDate 1"]
 *                             ...],
 *           drilldownData: [[name : "Date 1",
 *                            id   : "LocalAdjustmentsDate 1"
 *                            data : [[name     : "Global List Price",
 *                                     y        : 10,
 *                                     color    : "#BF4040"],
 *                                    [name     : "Foreign Exchange Adjustment",
 *                                     y        : 5,
 *                                     color    : "#BF4040"],
 *                                    [name     : "Local Adjustment",
 *                                     y        : 15,
 *                                     color    : "#BF4040"]
 *                                     ...]
 *  second : [:] <- same map as first but for the second comparison data
 */
Map getWaterfallChartData(Map sqlConfiguration,
                          Map dataForChart,
                          Map rawQueryData,
                          List waterfallConfigurationFields,
                          String selectedModel,
                          Map colors,
                          String drilldownName) {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig

    if (!dataForChart) {
        return [:]
    }

    boolean isDrilldown = isModelWithDrilldown(selectedModel)

    switch (selectedModel) {
        case commonsConstConfig.WATERFALL_MODEL_PERCENTAGE_NAME:
            return preparePercentageWaterfallData(dataForChart, waterfallConfigurationFields, colors, drilldownName, isDrilldown)
        case commonsConstConfig.WATERFALL_MODEL_ABSOLUTE_UNIT_NAME:
            BigDecimal totalQuantity = getTotalQuantity(rawQueryData, sqlConfiguration)

            return prepareAbsoluteUnitWaterfallData(totalQuantity, dataForChart, waterfallConfigurationFields, colors, drilldownName, isDrilldown)
        case commonsConstConfig.WATERFALL_MODEL_ABSOLUTE_NAME:
        case commonsConstConfig.WATERFALL_MODEL_DETAIL_NAME:
        default:
            return prepareAbsoluteWaterfallData(dataForChart, waterfallConfigurationFields, colors, drilldownName, isDrilldown)
    }
}

/**
 * Defines whether the provided waterfall model uses the drilldown functionality
 * @param selectedModel selected calculation model, the list of available models can be found in the SIP_Dashboards_Commons.ConstConfig
 * @return true if the model supports drilldown
 */
boolean isModelWithDrilldown(String selectedModel) {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig

    return selectedModel != commonsConstConfig.WATERFALL_MODEL_DETAIL_NAME
}

/**
 * Prepares the data structure for the waterfall chart.
 * The structure is based on the Waterfall Fields from the Configuration.WATERFALL_CONFIGURATION_FIELDS map.
 * Columns without sub-elements are presented as is, while the ones with sub-elements have them appended and defined with name, label and value.
 * The values in the elements may be also negated depending, whether the isSubtract flag is set.
 * Example structure:
 * - GlobalListPrice = 6.28E8
 * - LocalAdjustments
 *      a) name: ForeignExchangeAmount, label: Foreign Exchange Amount, value: -1.84E7
 *      b) name: LocalAdjustment, label: Local Adjustment, value: -4.40E7
 * - LocalListPrice = 5.65E8
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param queryData raw query data.
 * @return processed chart data containing structure with data as defined by the WATERFALL_CONFIGURATION_FIELDS
 */
Map processWaterfallFieldData(List<Map> waterfallConfigurationFields, Map queryData) {
    Map dataForChart = [:]
    if (!waterfallConfigurationFields) {
        return dataForChart
    }

    List subLevelData = []
    BigDecimal subtractFactor
    String firstFieldName = waterfallConfigurationFields.getAt(0).name
    BigDecimal currentSumValue = queryData?.getAt(firstFieldName)

    if (currentSumValue == null) {
        return [:]
    }

    waterfallConfigurationFields = getWaterfallFieldsWithData(waterfallConfigurationFields, queryData)
    markFirstFieldAsSum(waterfallConfigurationFields)

    for (Map field in waterfallConfigurationFields) {
        if (field.isSum) {
            dataForChart << [(field.label): currentSumValue]
        } else {
            if (field.subLevel) {
                subLevelData = getProcessedSubLevelData(field.subLevel, queryData)
                dataForChart << [(field.label): subLevelData]
                currentSumValue += subLevelData.value.sum()
            } else {
                subtractFactor = field.isSubtract == true ? -1 : 1
                BigDecimal elementValue = queryData?.getAt(field.name)?.multiply(subtractFactor) ?: BigDecimal.ZERO
                dataForChart << [(field.label): elementValue]
                currentSumValue += elementValue
            }
        }
    }

    return dataForChart
}

/**
 * Marks the first fields as a sum for proper display on the chart.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 */
protected void markFirstFieldAsSum(List<Map> waterfallConfigurationFields) {
    waterfallConfigurationFields.getAt(0).isSum = true
}

/**
 * Retrieves the total quantity from the provided raw query data.
 * Total quantity is required for PerUnit calculations.
 * In case of invalid quantity (0, null) an exception is thrown.
 * @param rawQueryData raw query data retrieved based on waterfall fields and additional quantity field
 * @param sqlConfiguration sql configuration containing data mart fields mapping definition. Stored in SIP_AdvancedConfiguration.
 * @return total quantity value used for per unit calculations.
 */
protected BigDecimal getTotalQuantity(Map rawQueryData, Map sqlConfiguration) {
    BigDecimal totalQuantity = rawQueryData?.getAt(sqlConfiguration.quantity)

    if (!totalQuantity) {
        api.throwException("Total quantity of selected items is invalid")
    }

    return totalQuantity
}

/**
 * Retrieves those waterfall fields that have data retrieved from the query.
 * These fields cannot be marked as isSum and cannot have sub elements
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param rawQueryData raw query data retrieved based on waterfall fields and additional quantity field
 * @return a List of waterConfigurationFields that have a data representation in the provided queryData structure
 */
protected List getWaterfallFieldsWithData(List<Map> waterfallConfigurationFields, Map queryData) {
    return waterfallConfigurationFields.findAll { Map field -> field.isSum || queryData?.getAt(field.name) != null || field.subLevel.any { queryData?.getAt(it.name) != null } }
}

/**
 * Prepares the main series and drilldown data used for the absolute waterfall models.
 * The returned structure looks like this:
 * [mainSeriesData: mainSeriesData,
 *  drilldownData : drilldownData]
 * @param rawChartData processed data for the chart returned from the processWaterfallFieldData.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param colors a map of colors used for the series
 * @param drilldownName name of the drilldown, used as a suffix for the series to differentiate.
 * @param isDrilldown true in case the waterfall supports drilldown functionality
 * @return a structure containing all data for the absolute waterfall models
 */
Map prepareAbsoluteWaterfallData(Map rawChartData,
                                 List waterfallConfigurationFields,
                                 Map colors,
                                 String drilldownName,
                                 boolean isDrilldown) {
    Closure valueCalculation = { BigDecimal valueData -> return valueData }

    return prepareWaterfallData(rawChartData, waterfallConfigurationFields, colors, drilldownName, isDrilldown, valueCalculation)
}

/**
 * Prepares the main series and drilldown data used for the percentage waterfall model.
 * The returned structure looks like this:
 * [mainSeriesData: mainSeriesData,
 *  drilldownData : drilldownData]
 * @param rawChartData processed data for the chart returned from the processWaterfallFieldData.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param colors a map of colors used for the series
 * @param drilldownName name of the drilldown, used as a suffix for the series to differentiate.
 * @param isDrilldown true in case the waterfall supports drilldown functionality
 * @return a structure containing all data for the percentage waterfall model
 */
Map preparePercentageWaterfallData(Map rawChartData, List waterfallConfigurationFields, Map colors, String drilldownName, boolean isDrilldown) {
    Closure valueCalculation = getPercentageValueCalculationClosure(waterfallConfigurationFields, rawChartData)

    return prepareWaterfallData(rawChartData, waterfallConfigurationFields, colors, drilldownName, isDrilldown, valueCalculation)
}

/**
 * Prepares the main series and drilldown data used for the per unit waterfall model.
 * The returned structure looks like this:
 * [mainSeriesData: mainSeriesData,
 *  drilldownData : drilldownData]
 * @param totalQuantity total quantity of elements returned from the query. Calculated by getTotalQuantity
 * @param rawChartData processed data for the chart returned from the processWaterfallFieldData.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param colors a map of colors used for the series
 * @param drilldownName name of the drilldown, used as a suffix for the series to differentiate.
 * @param isDrilldown true in case the waterfall supports drilldown functionality
 * @return a structure containing all data for the per unit waterfall model
 */
Map prepareAbsoluteUnitWaterfallData(BigDecimal totalQuantity,
                                     Map rawChartData,
                                     List waterfallConfigurationFields,
                                     Map colors,
                                     String drilldownName,
                                     boolean isDrilldown) {
    Closure valueCalculation = { BigDecimal valueData -> return valueData / totalQuantity }

    return prepareWaterfallData(rawChartData, waterfallConfigurationFields, colors, drilldownName, isDrilldown, valueCalculation)
}

/**
 * Retrieves the calculation closure used for percentage model value calculations.
 * Each objects value is calculated using the calculation closure
 * The percentage base for the calculations is retrieved from the field marked as percentageBase.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param rawChartData processed data for the chart returned from the processWaterfallFieldData.
 * @return a Closure defining the calculation closure for percentage model calculations.
 */
protected Closure getPercentageValueCalculationClosure(List waterfallConfigurationFields, Map rawChartData) {
    BigDecimal denominatorValue = getDenominatorFieldValue(waterfallConfigurationFields, rawChartData)

    return denominatorValue ? { BigDecimal value -> return libs.SIP_Dashboards_Commons.MathUtils.getPercentageRatio(value, denominatorValue) } : null
}

/**
 * Retrieves the raw query data used for the waterfall dashboards.
 * The structure of the query is build based on the waterfallConfigurationFields.
 * All user input filters are applied if in use.
 * @param sqlConfiguration sql configuration containing data mart fields mapping definition. Stored in SIP_AdvancedConfiguration.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param targetCurrencyCode currency code that the dashboards is going to be displayed in. Used for currency conversion.
 * @param productGroup product group as generated by the ProductInput entry user input.
 *                     Provides information from which product group the data should be fetched.
 * @param customerGroup customer group as generated by the CustomerInput entry user input.
 *                      Provides information from which customer group the data should be fetched.
 * @param dateFrom date from as generated by the DateFromInput entry user input. Provides starting date for the retrieved data.
 * @param dateTo date to as generated by the DateToInput entry user input. Provides end date for the retrieved data.
 * @param genericFilter generic filter as generated by the generic filter user input.
 * @return a raw query data for the waterfall dashboards. The names of the elements returned are based on waterfallConfigurationFields.
 */
Map getWaterfallQueryResult(Map sqlConfiguration,
                            List<Map> waterfallConfigurationFields,
                            String targetCurrencyCode,
                            ProductGroup productGroup,
                            CustomerGroup customerGroup,
                            String dateFrom,
                            String dateTo,
                            Filter genericFilter) {
    def queryUtils = libs.SIP_Dashboards_Commons.QueryUtils
    Map queryDef = generateBaseWaterfallQuery(sqlConfiguration.datamartName, sqlConfiguration.quantity, waterfallConfigurationFields)
    List dateFilters = queryUtils.generateDateFromToFilters(dateFrom,
            dateTo,
            sqlConfiguration.pricingDate)

    queryDef.whereFilters = [productGroup,
                             customerGroup,
                             *dateFilters,
                             genericFilter]

    queryDef.targetCurrencyCode = targetCurrencyCode

    Map datamartQueryResult = libs.HighchartsLibrary.QueryModule.queryDatamart(queryDef)?.getAt(0)

    List missingFields = findMissingWaterfallFields(waterfallConfigurationFields, datamartQueryResult)

    return missingFields ? null : datamartQueryResult
}

/**
 * Adds the comparison dimension parameter for Waterfall Comparison Dashboard.
 * The parameter allows the selection of dimension in which the dashboard should be displayed.
 * The available comparison dimensions are defined in libs.SIP_Dashboards_Commons.ConstConfig.WATERFALL_COMPARISON_DASHBOARD_CONFIG
 * Default value is Date.
 * User default values are applied if applicable.
 * @param configuratorEntry the configurator entry to which the parameter will be appended
 * @param isCustomerUsed states whether the customer data is mapped and used by the package
 * @return the ContextParameter object containing the definition of Comparison model parameter.
 */
def addComparisonDimensionParameter(def configuratorEntry, boolean isCustomerUsed) {
    Map dashboardConfig = libs.SIP_Dashboards_Commons.ConstConfig.WATERFALL_COMPARISON_DASHBOARD_CONFIG
    Map inputConfig = dashboardConfig.INPUTS.COMPARISON_DIMENSIONS
    Map comparisonDimensionsConfig = dashboardConfig.COMPARISON_DIMENSIONS

    if (!isCustomerUsed) {
        comparisonDimensionsConfig.remove(libs.SIP_Dashboards_Commons.ConstConfig.COMPARISON_DIMENSION_CUSTOMER_KEY)
    }

    Map configuratorConfig = libs.SIP_Dashboards_Commons.ConfiguratorUtils.getDashboardInputOptionKeyLabelConfig(comparisonDimensionsConfig)

    def parameter = configuratorEntry.createParameter(InputType.OPTION, inputConfig.UNIQUE_KEY)
            .setLabel(inputConfig.LABEL)

    parameter.setValueOptions(configuratorConfig.values)
            .addParameterConfigEntry("labels", configuratorConfig.labels)

    libs.SIP_Dashboards_Commons.ConfiguratorUtils.setParameterDefaultValue(parameter, inputConfig.DEFAULT_VALUE)

    return parameter
}

/**
 * Adds the parameter to a given configurator for the first comparison parameter.
 * In case the selected dimension is equal to the input dimension of a given parameter a label with suffix (1) is applied.
 * @param configurator the configurator entry to which the parameter will be appended
 * @param comparisonDimension comparison dimension for the parameter.
 *                            The available comparison dimensions are defined in libs.SIP_Dashboards_Commons.ConstConfig.WATERFALL_COMPARISON_DASHBOARD_CONFIG
 * @param inputDimension comparison dimension selected by the user in the ComparisonDimensionEntry.
 * @param inputLabel label with the suffix
 * @param inputDefaultLabel label without the suffix
 * @param inputKey unique input key with the suffix
 * @param inputDefaultKey unique input key without the suffix
 * @param defaultValue default value to be used in case no userDefaultValue is defined and no value has been selected by the user.
 * @return a parameter defining the first comparison parameter
 */
def addPrimaryComparisonParameter(def configurator,
                                  String comparisonDimension,
                                  String inputDimension,
                                  String inputLabel,
                                  String inputDefaultLabel,
                                  String inputKey,
                                  String inputDefaultKey,
                                  def defaultValue) {
    if (!comparisonDimension) {
        return null
    }

    String finalInputLabel = comparisonDimension == inputDimension ? inputLabel : inputDefaultLabel
    String finalInputKey = comparisonDimension == inputDimension ? inputKey : inputDefaultKey

    return addComparisonInputParameter(configurator, inputDimension, finalInputKey, finalInputLabel, defaultValue)
}

/**
 * Adds the parameter to a given configurator for the second comparison parameter.
 * In case the selected dimension is equal to the input dimension of a given parameter a label with suffix (2) is applied.
 * A null is returned otherwise.
 * @param configurator the configurator entry to which the parameter will be appended
 * @param comparisonDimension comparison dimension for the parameter.
 *                            The available comparison dimensions are defined in libs.SIP_Dashboards_Commons.ConstConfig.WATERFALL_COMPARISON_DASHBOARD_CONFIG
 * @param inputDimension comparison dimension selected by the user in the ComparisonDimensionEntry.
 * @param inputLabel label with the suffix
 * @param inputKey unique input key with the suffix
 * @param defaultValue default value to be used in case no userDefaultValue is defined and no value has been selected by the user.
 * @return a parameter defining the second comparison parameter
 */
def addSecondaryComparisonParameter(def configurator,
                                    String comparisonDimension,
                                    String inputDimension,
                                    String inputLabel,
                                    String inputKey,
                                    def defaultValue) {
    return comparisonDimension == inputDimension ? addComparisonInputParameter(configurator, inputDimension, inputKey, inputLabel, defaultValue)
            : null
}

/**
 * Retrieves a date that's defined by the adjustments provided as parameters.
 * @param yearAdjustment defines the offset from the current year for the year to be returned.
 * @param month defines the month of the year
 * @param day defines the day of the month
 * @return a Date object
 */
Date getDate(int yearAdjustment, int month, int day) {
    Calendar defaultDate = Calendar.getInstance()
    defaultDate.add(Calendar.YEAR, yearAdjustment)
    defaultDate.set(Calendar.MONTH, month)
    defaultDate.set(Calendar.DAY_OF_MONTH, day)

    return defaultDate.getTime()
}

/**
 * Retrieves the appropriate title based on the selected model.
 * The title mapping is stored in appropriate waterfall dashboard configuration under MODELS key
 * @param waterfallConfig waterfall dashboard configuration stored in libs.SIP_Dashboards_Commons.ConstConfig
 * @param selectedModel selected calculation model, the list of available models can be found in the SIP_Dashboards_Commons.ConstConfig
 * @return a title for a given model
 */
String getChartModelTitle(Map waterfallConfig, String selectedModel) {
    return waterfallConfig.MODELS.getAt(selectedModel).TITLE
}

/**
 * Retrieves the format for the tooltip based on the provided calculation model.
 * Used to determine whether a percentage, price or absolute price format is required.
 * The formats are stored in libs.HighchartsLibrary.ConstConfig.
 * @param selectedModel selected calculation model, the list of available models can be found in the SIP_Dashboards_Commons.ConstConfig
 * @return appropriate format for a given calculation model.
 */
String getNumberTooltipFormat(String selectedModel) {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig
    def hLibConstConfig = libs.HighchartsLibrary.ConstConfig
    switch (selectedModel) {
        case commonsConstConfig.WATERFALL_MODEL_ABSOLUTE_NAME:
            return hLibConstConfig.TOOLTIP_POINT_Y_FORMAT_TYPES.ABSOLUTE_PRICE
        case commonsConstConfig.WATERFALL_MODEL_PERCENTAGE_NAME:
            return hLibConstConfig.TOOLTIP_POINT_Y_FORMAT_TYPES.PERCENTAGE
        case commonsConstConfig.WATERFALL_MODEL_DETAIL_NAME:
            return hLibConstConfig.TOOLTIP_POINT_Y_FORMAT_TYPES.ABSOLUTE_PRICE
        case commonsConstConfig.WATERFALL_MODEL_ABSOLUTE_UNIT_NAME:
            return hLibConstConfig.TOOLTIP_POINT_Y_FORMAT_TYPES.PRICE
        default:
            return hLibConstConfig.TOOLTIP_POINT_Y_FORMAT_TYPES.ABSOLUTE_PRICE
    }
}

/**
 * Retrieves the appropriate Y axis label based on the selected calculation model.
 * @param selectedModel selected calculation model, the list of available models can be found in the SIP_Dashboards_Commons.ConstConfig
 * @param currencyCode currency code of the selected currency in UserCurrency. Used for currency conversions.
 * @return Y axis label for a given calculation model
 */
String getYAxisLabel(String selectedModel, String currencyCode) {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig

    switch (selectedModel) {
        case commonsConstConfig.WATERFALL_MODEL_ABSOLUTE_NAME:
        case commonsConstConfig.WATERFALL_MODEL_ABSOLUTE_UNIT_NAME:
        case commonsConstConfig.WATERFALL_MODEL_DETAIL_NAME:
            return currencyCode
        case commonsConstConfig.WATERFALL_MODEL_PERCENTAGE_NAME:
            return "Percentage (%)"
        default:
            return currencyCode
    }
}

/**
 * Retrieves the appropriate data tooltip format based on the selected calculation model.
 * @param selectedModel selected calculation model, the list of available models can be found in the SIP_Dashboards_Commons.ConstConfig
 * @param currencyCode currency code of the selected currency in UserCurrency. Used for currency conversions.
 * @return data tooltips display format, used to properly display absolute and percentage values
 */
String getDataTooltipFormat(String selectedModel, String numberFormat, String currencySymbol) {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig

    switch (selectedModel) {
        case commonsConstConfig.WATERFALL_MODEL_ABSOLUTE_NAME:
        case commonsConstConfig.WATERFALL_MODEL_DETAIL_NAME:
        case commonsConstConfig.WATERFALL_MODEL_ABSOLUTE_UNIT_NAME:
            return libs.SIP_Dashboards_Commons.CurrencyUtils.getFormatWithCurrencySymbol(numberFormat, currencySymbol)
        case commonsConstConfig.WATERFALL_MODEL_PERCENTAGE_NAME:
            return libs.HighchartsLibrary.ConstConfig.TOOLTIP_POINT_Y_FORMAT_TYPES.PERCENTAGE
        default:
            return numberFormat
    }
}

/**
 * Finds and returns any defined waterfall fields that do not have data returned from the query.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param datamartQueryResult result of the main waterfall query based on the provided waterfallConfigurationFields
 * @return List of waterfallConfigurationFields that do not have data in waterfallConfigurationFields
 */
protected List<Map> findMissingWaterfallFields(List<Map> waterfallConfigurationFields, Map datamartQueryResult) {
    return waterfallConfigurationFields.findAll { Map field ->
        boolean isFieldDataMissing = field.subLevel ?
                field.subLevel.any { Map subLevel -> datamartQueryResult.getAt(subLevel.name) == null } :
                datamartQueryResult?.getAt(field.name) == null

        return !field.isSum && isFieldDataMissing
    }
}

/**
 * Retrieves the Highchart compliant structure for a data points in waterfall charts.
 * @param name name for the entry
 * @param value value of the entry
 * @param color hex value of color for the entry
 * @param colors a map of colors used for the series
 * @param drilldownId ID for the drilldown series, used to match the main series with corresponding drilldown
 * @param isSum defines whether the given data point is marked as a sum
 * @return a Highchart compliant structure for a waterfall data point
 */
protected Map getFieldDataStructure(String name,
                                    BigDecimal value,
                                    String color,
                                    Map colors,
                                    String drilldownId = null,
                                    Boolean isSum = null) {
    Map structure = [name : name,
                     y    : value,
                     color: color]

    if (!color) {
        structure << [color: getColor(value, colors)]
    }

    if (drilldownId) {
        structure << [drilldown: drilldownId]
    }

    if (isSum) {
        structure << [isSum: isSum]
    }

    return structure
}

/**
 * Retrieves the definition for the base query used in the waterfall dashboards.
 * The base query is then extended with proper filters depending on the dashboard.
 * @param dmName name of the datamart to retrieve the data from. Mapped in SQL_CONFIGURATION.
 * @param quantityField field from the datamart that defines the column holding quantity. Mappend in SQL_CONFIGURATION.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @return definition of the query as defined by the Dashboard Library
 */
protected Map generateBaseWaterfallQuery(String dmName, String quantityField, List<Map> waterfallConfigurationFields) {
    return [datamartName: dmName,
            rollup      : true,
            fields      : [(quantityField): quantityField] + getWaterfallQueryFields(waterfallConfigurationFields)]
}

/**
 * Retrieves the query fields based on the provided waterfallConfigurationFields structure.
 * The fields marked as isSum are skipped (except for the first one)
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @return a structure of [[(fieldAlias): field], ...] defining the query fields
 */
protected Map getWaterfallQueryFields(List waterfallConfigurationFields) {
    Map waterfallFields = [:]

    for (Map field in waterfallConfigurationFields) {
        if (field.isSum && !isFirstField(field, waterfallConfigurationFields)) {
            continue
        }

        waterfallFields << getQueryFieldStructure(field)
    }

    return waterfallFields
}

/**
 * Retrieves the query fields from a single waterfall field definition.
 * In case the field contains sub elements all of them are queried.
 * In the other case just the field is queried.
 * @param field waterfall field definition as defined by getWaterfallFieldsDefinition
 * @return Map containing all required data fields to be fetched from the DM for the given waterfall field
 */
protected Map getQueryFieldStructure(Map field) {
    List<Map> subLevel = field.subLevel

    if (!subLevel) {
        return [(field.name): field.name]
    }

    return subLevel.collectEntries { Map subElement -> [(subElement.name): subElement.name] }
}

//TODO: Add warning when such column is missing during Error handling task https://pricefx.atlassian.net/browse/PFPCS-1663
/**
 * Retrieves the percentage base value.
 * If the waterfall field marked as a percentage base contains sub elements the value of the percentage base is a sum.
 * In the other case just the value of the field is returned
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param rawChartData processed data for the chart returned from the processWaterfallFieldData.
 * @return a value of the percentage base used for percentage calculations.
 */
protected BigDecimal getDenominatorFieldValue(List<Map> waterfallConfigurationFields, Map rawChartData) {
    Map denominatorField = waterfallConfigurationFields.find { it.isPercentBase == true }

    return denominatorField?.subLevel ? rawChartData?.getAt(denominatorField?.label)?.value?.sum() : rawChartData?.getAt(denominatorField?.label)
}

/**
 * Retrieves the processed data for a single sub level.
 * The value of the sub level element is reversed in case the isSubtract flag is set.
 * The returned structure is defined:
 * [label : label of the field,
 *  value : value of the field from query data, 0 otherwise]
 * @param subLevel a sub level structure for a given waterfall field. Structure defined in getWaterfallFieldsDefinition
 * @param queryData raw query data.
 * @return a structure for a single sub level entry
 */
protected List getProcessedSubLevelData(List subLevel, Map queryData) {
    return subLevel.findAll { queryData?.getAt(it.name) != null }
            .collect { Map field ->
                BigDecimal subtractFactor = field.isSubtract == true ? -1 : 1

                return [label: field.label,
                        value: subtractFactor * queryData?.getAt(field.name) ?: BigDecimal.ZERO]
            }
}

/**
 * Retrieves the data structure containing both the main series and the drilldown data for the waterfall dashboards.
 * The data is returned only in case both waterfallConfigurationFields and value calculation closure are valid.
 * @param rawChartData processed data for the chart returned from the processWaterfallFieldData.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param colors a map of colors used for the series
 * @param drilldownName name of the drilldown, used as a suffix for the series to differentiate.
 * @param isDrilldown true in case the waterfall supports drilldown functionality
 * @param valueCalculation calculation closure defining how each data point value should be calculated/modified.
 * @return structure containing main series and drilldown series data for the waterfall dashboards
 */
protected Map prepareWaterfallData(Map rawChartData,
                                   List waterfallConfigurationFields,
                                   Map colors,
                                   String drilldownName,
                                   boolean isDrilldown,
                                   Closure valueCalculation) {
    List mainSeriesData = []
    List drilldownData = []

    if (waterfallConfigurationFields && valueCalculation) {
        mainSeriesData = generateLevelData(rawChartData, waterfallConfigurationFields, isDrilldown, colors, drilldownName, valueCalculation)
        if (isDrilldown) {
            drilldownData = generateDrilldownData(rawChartData, waterfallConfigurationFields, colors, drilldownName, valueCalculation)
        }
    }

    return [mainSeriesData: mainSeriesData,
            drilldownData : drilldownData]
}

/**
 * Prepares the drilldown data based on provided waterfallConfigurationFields.
 * @param dataForChart processed data for the chart returned from the processWaterfallFieldData.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param colors a map of colors used for the series
 * @param drilldownName name of the drilldown, used as a suffix for the series to differentiate.
 * @param valueCalculationClosure calculation closure defining how each data point value should be calculated/modified.
 * @return List of all drilldown elements for a given waterfallConfigurationFields. The structure looks like this:
 * ["name": "Drilldown",
 *  "id": "Local AdjustmentsDrilldown",
 *  "data" : [...]]
 */
protected List generateDrilldownData(Map dataForChart,
                                     List waterfallConfigurationFields,
                                     Map colors,
                                     String drilldownName,
                                     Closure valueCalculationClosure) {
    List drilldownData = []
    Map fieldDrilldownData

    waterfallConfigurationFields.eachWithIndex { currentField, int index ->
        fieldDrilldownData = getFieldDrilldownData(dataForChart, currentField, waterfallConfigurationFields, index, colors, drilldownName, valueCalculationClosure)
        if (fieldDrilldownData) {
            drilldownData << fieldDrilldownData
        }
    }

    return drilldownData
}

/**
 * Generates the data structure for a given waterfall level - whether that is a main series or a part of a drilldown structure.
 * The data generated is based on the waterfallConfigurationFields and the configuration of each field.
 * In case the field has a subLevel and is not marked as sum and the sub level drilldown:
 *  - is used - one structure is generated that has a value of all sub elements (high level)
 *  - is not used - each sub level element is appended to the regular data (drilldown)
 * In case the field does not have a sub level
 *  - the field is a sum - therefore appropriate color is assigned and a sum structure is created
 *  - the field is not a sum - therefore appropriate color is assigned and a gain structure is created
 * @param dataForChart processed data for the chart returned from the processWaterfallFieldData.
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param isSubLevelDrilldown defines whether the drilldown should be used
 * @param colors a map of colors used for the series
 * @param drilldownName name of the drilldown, used as a suffix for the series to differentiate.
 * @param valueCalculationClosure calculation closure defining how each data point value should be calculated/modified.
 * @param ignoreFirstSum defines whether the first sum condition should be ignored.
 *                       The condition states whether the first sum column has been already met, used for proper data display.
 * @return waterfall level data, can either be main series structure or drilldown
 */
protected List generateLevelData(Map dataForChart,
                                 List waterfallConfigurationFields,
                                 boolean isSubLevelDrilldown,
                                 Map colors,
                                 String drilldownName,
                                 Closure valueCalculation,
                                 boolean ignoreFirstSum = false) {
    def predefinedColor, value
    Boolean isFirstSumMet = ignoreFirstSum
    List levelData = []

    for (Map field : waterfallConfigurationFields) {
        if (field.subLevel && !field.isSum) {
            if (isSubLevelDrilldown) {
                List fieldValues = dataForChart.getAt(field.label)
                BigDecimal fieldValuesSum = fieldValues?.sum { it.value }
                value = valueCalculation.call(fieldValuesSum)
                levelData += getFieldDataStructure(field.label, value, null, colors, field.label + drilldownName)
            } else {
                for (element in dataForChart.getAt(field.label)) {
                    levelData += getFieldDataStructure(element.label, element.value, null, colors)
                }
            }
        } else {
            BigDecimal fieldValue = dataForChart.getAt(field.label)
            value = valueCalculation.call(fieldValue)
            predefinedColor = field.isSum ? colors.PRICE : null
            levelData += getFieldDataStructure(field.label, value, predefinedColor, colors, null, isFirstSumMet && field.isSum)
            if (!isFirstSumMet && field.isSum) {
                isFirstSumMet = true
            }
        }
    }

    return levelData
}

/**
 * States whether the provided field is the first one in waterfallConfigurationFields.
 * Used to append its value for the query fetch method
 * @param field field definition from waterfallConfigurationFields
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @return true if field is the first one in waterfallConfigurationFields
 */
protected boolean isFirstField(Map field, List<Map> waterfallConfigurationFields) {
    return field.name == waterfallConfigurationFields[0].name
}

/**
 * Retrieves the drilldown data for a given waterfall field from waterfallConfigurationFields.
 * If the given field has sub elements they are all appended to the drilldown level data that is returned.
 * The structure of the field is defined as:
 * [name: drilldownName,
 *  id  : drilldownId,
 *  data: drilldownData]
 * @param dataForChart processed data for the chart returned from the processWaterfallFieldData.
 * @param currentField field definition from waterfallConfigurationFields
 * @param waterfallConfigurationFields field configuration from getWaterfallFieldsDefinition.
 * @param index index of the currentField in the waterfallConfigurationFields list
 * @param colors a map of colors used for the series
 * @param drilldownName name of the drilldown, used as a suffix for the series to differentiate.
 * @param valueCalculationClosure calculation closure defining how each data point value should be calculated/modified.
 * @return a Map containing the drilldown definition for a given waterfall field.
 */
protected Map getFieldDrilldownData(Map dataForChart,
                                    Map currentField,
                                    List waterfallConfigurationFields,
                                    int index,
                                    Map colors,
                                    String drilldownName,
                                    Closure valueCalculationClosure) {
    Map drilldownData = [:]
    Integer previousIsSumFieldIndex, nextSumFieldIndex
    def value
    List subData, levelData

    subData = []
    if (!currentField.isSum) {
        previousIsSumFieldIndex = 0
        nextSumFieldIndex = waterfallConfigurationFields?.size() - 1
        levelData = generateLevelData(dataForChart, waterfallConfigurationFields.subList(previousIsSumFieldIndex, index), true, colors, drilldownName, valueCalculationClosure)
        subData.addAll(levelData)

        if (currentField.subLevel) {
            dataForChart?.getAt(currentField.label).each { Map it ->
                value = valueCalculationClosure.call(it.value)
                subData += getFieldDataStructure(it.label, value, null, colors)
            }
        } else {
            value = valueCalculationClosure.call(dataForChart?.getAt(currentField.label))
            subData += getFieldDataStructure(currentField.label, value, null, colors)
        }

        levelData = generateLevelData(dataForChart, waterfallConfigurationFields.subList(index + 1, nextSumFieldIndex + 1), true, colors, drilldownName, valueCalculationClosure, true)
        subData.addAll(levelData)
        drilldownData = [name: drilldownName,
                         id  : currentField.label + drilldownName,
                         data: subData]
    }

    return drilldownData
}

/**
 * Retrieves the color that should be used to display a given field based on its value
 * @param value value of the field
 * @param colors a map of colors used for the series
 * @return a String defining the hex value of a color
 */
protected String getColor(BigDecimal value, Map colors) {
    return (value > 0 ? colors.POSITIVE : colors.NEGATIVE)
}

/**
 * Adds the comparison input parameter used in the Comparison Waterfall to the given configurator.
 * The type of the input depends in the provided input dimension
 * @param configurator the configurator entry to which the parameter will be appended
 * @param inputDimension comparison dimension selected by the user in the ComparisonDimensionEntry.
 * @param uniqueKey unique key for the input parameter, used to differentiate between different parameters
 * @param label label to be used for the entry
 * @param defaultValue default value to be used in case no userDefaultValue is defined and no value has been selected by the user.
 * @param userDefaultValue value defined by the user for the given entry in the Default Filters configurator wizard.
 * @return parameter defining the comparison dimension input
 */
protected def addComparisonInputParameter(def configurator,
                                          String inputDimension,
                                          String uniqueKey,
                                          String label,
                                          def defaultValue) {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig
    Map inputMap = [(commonsConstConfig.COMPARISON_DIMENSION_PRODUCT_KEY) : InputType.PRODUCTGROUP,
                    (commonsConstConfig.COMPARISON_DIMENSION_CUSTOMER_KEY): InputType.CUSTOMERGROUP,
                    (commonsConstConfig.COMPARISON_DIMENSION_DATE_KEY)    : InputType.DATEUSERENTRY]

    def parameter = configurator.createParameter(inputMap.getAt(inputDimension), uniqueKey)
            .setLabel(label)
    libs.SIP_Dashboards_Commons.ConfiguratorUtils.setParameterDefaultValue(parameter, defaultValue)

    return parameter
}