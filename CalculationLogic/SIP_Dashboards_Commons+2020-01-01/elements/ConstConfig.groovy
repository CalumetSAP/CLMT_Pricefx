import groovy.transform.Field
import net.pricefx.common.api.FieldFormatType
import net.pricefx.common.api.InputType

/**
 * Stores the name of the PP table used for retrieving the currency symbols
 * Structure of an entry is:
 * [name : "EUR",
 *  value: "€"]
 */
@Field String CURRENCY_SYMBOL_PP_NAME = "CurrencySymbols"

/**
 * Stores the configuration of the PP table responsible for storing the user defined default filters for all the dashboards.
 */
@Field Map DASHBOARDS_DEFAULT_FILTERS_PP_CONFIG = [NAME         : "SIP_DefaultFilterValues",
                                                   COLUMN_CONFIG: [FILTER_NAME : "key1",
                                                                   USER_NAME   : "key2",
                                                                   FILTER_VALUE: "attributeExtension___FilterValue"]]

/**
 * Name of the Advanced Configuration for the package, contains all DM field mappings.
 */
@Field String DASHBOARDS_ADVANCED_CONFIGURATION_NAME = "SIP_AdvancedConfiguration"

/**
 * Name of the Advanced Configuration for the SIP_Dashboards_Commons, contains information about the ccy DS.
 */
@Field String COMMONS_ADVANCED_CONFIGURATION_NAME = "SIP_Commons_AdvancedConfiguration"

/**
 * Defines the key of Empty dimension used in Causality dashboards.
 * Allows to aggregate over only one product or customer dimension.
 */
@Field String EMPTY_DIMENSION_KEY = "none"

/**
 * Defines the value of Empty dimension used in Causality dashboards.
 * Allows to aggregate over only one product or customer dimension.
 */
@Field String EMPTY_DIMENSION_VALUE = "None"

/**
 * Defines the value of an entry that is marked as "used" in PP tables.
 * Used mainly in Regional Revenue and Margin Dashboard
 */
@Field String IS_USED_VALUE = "Yes"

/**
 * Provides an EMPTY_DIMENSION map that is used in the QueryUtils getNameLabelPairs method to retrieve available aggregation dimensions
 */
@Field Map EMPTY_DIMENSION = [(EMPTY_DIMENSION_KEY): EMPTY_DIMENSION_VALUE]

/**
 * Defines the key used to describe the margin contribution KPI in various dashboards.
 */
@Field String MARGIN_CONTRIBUTION_PERCENTAGE_KPI_KEY = "marginContributionPercentage"

/**
 * Defines the key used to describe the revenue contribution KPI in various dashboards.
 */
@Field String REVENUE_CONTRIBUTION_PERCENTAGE_KPI_KEY = "revenueContributionPercentage"

/**
 * Defines the key used to describe the item name KPI in various dashboards.
 */
@Field String NAME_KPI_KEY = "itemName"

/**
 * Defines the key used to describe the item number (or ID) KPI in various dashboards.
 */
@Field String ITEM_NUMBER_KPI_KEY = "itemNumber"

/**
 * Defines the key used to describe the revenue KPI in various dashboards.
 */
@Field String REVENUE_KPI_KEY = "revenue"

/**
 * Defines the key used to describe the margin percentage KPI in various dashboards.
 */
@Field String MARGIN_PERCENTAGE_KPI_KEY = "marginPercentage"

/**
 * Defines the key used to describe the volume KPI in various dashboards.
 */
@Field String VOLUME_KPI_KEY = "volume"

/**
 * Defines the key used to describe the margin KPI in various dashboards.
 */
@Field String MARGIN_KPI_KEY = "margin"

/**
 * Defines the key used to describe the KPI in Outliers Dashboard.
 */
@Field String KPI_KEY = "kpi"

/**
 * Defines the key used to describe the bucket in Outliers Dashboard.
 */
@Field String BUCKET_KEY = "bucket"


/**
 * Defines the key used to describe the min max calculation model in Outliers Dashboard.
 */
@Field String MAX_MIN_SPLIT_MODEL_KEY = "minMaxSplit"

/**
 * Defines the key used to describe the equal split calculation model in Outliers Dashboard.
 */
@Field String EQUAL_SPLIT_MODEL_KEY = "equalSplit"

/**
 * Defines the key used to describe the contribution calculation model in Outliers Dashboard.
 */
@Field String CONTRIBUTION_MODEL_KEY = "contribution"

/**
 * Holds the configuration for the Outliers Dashboard configurator logic.
 */
@Field Map OUTLIERS_CONFIGURATOR_CONFIG = [NAME: "Configurator_Outliers"]

/**
 * Holds all configuration of the Outliers Dashboard.
 * The configuration contains
 * - FIELDS - definition of all KPI used by the dashboard and their formats
 * - BASE_FIELDS - defines which fields are used in both of the Performance matrices
 * - PIE_CHART_DATA_COMMON_COLUMNS - defines which fields are used in both of the pie charts
 * - ADDITIONAL_DATA_PER_DIMENSION - defines additional fields to be used by pie charts and performance matrices depending on the displayed dimension
 * - MODELS - contains additional information for the used calculation models
 * - INPUTS - contains all information about available user inputs used in the dashboard. Used to generate default filters.
 */
@Field Map OUTLIERS_DASHBOARD_CONFIG = [FIELDS                       : [(MARGIN_CONTRIBUTION_PERCENTAGE_KPI_KEY) : [LABEL        : "Margin Contribution %",
                                                                                                                    RESULT_MATRIX: FieldFormatType.PERCENT],
                                                                        (REVENUE_CONTRIBUTION_PERCENTAGE_KPI_KEY): [LABEL        : "Revenue Contribution %",
                                                                                                                    RESULT_MATRIX: FieldFormatType.PERCENT],
                                                                        (NAME_KPI_KEY)                           : [LABEL        : "Name",
                                                                                                                    RESULT_MATRIX: null],
                                                                        (ITEM_NUMBER_KPI_KEY)                    : [LABEL        : "Number",
                                                                                                                    RESULT_MATRIX: null],
                                                                        (REVENUE_KPI_KEY)                        : [LABEL        : "Revenue",
                                                                                                                    RESULT_MATRIX: FieldFormatType.MONEY],
                                                                        (MARGIN_PERCENTAGE_KPI_KEY)              : [LABEL        : "Margin %",
                                                                                                                    RESULT_MATRIX: FieldFormatType.PERCENT],
                                                                        (VOLUME_KPI_KEY)                         : [LABEL        : "Volume",
                                                                                                                    RESULT_MATRIX: FieldFormatType.NUMERIC],
                                                                        (MARGIN_KPI_KEY)                         : [LABEL        : "Margin",
                                                                                                                    RESULT_MATRIX: FieldFormatType.MONEY]],
                                        BASE_FIELDS                  : [NAME_KPI_KEY,
                                                                        ITEM_NUMBER_KPI_KEY,
                                                                        REVENUE_KPI_KEY,
                                                                        MARGIN_KPI_KEY,
                                                                        MARGIN_PERCENTAGE_KPI_KEY,
                                                                        MARGIN_CONTRIBUTION_PERCENTAGE_KPI_KEY,
                                                                        REVENUE_CONTRIBUTION_PERCENTAGE_KPI_KEY],
                                        PIE_CHART_DATA_COMMON_COLUMNS: [REVENUE_KPI_KEY,
                                                                        REVENUE_CONTRIBUTION_PERCENTAGE_KPI_KEY,
                                                                        MARGIN_KPI_KEY,
                                                                        MARGIN_PERCENTAGE_KPI_KEY,
                                                                        MARGIN_CONTRIBUTION_PERCENTAGE_KPI_KEY],
                                        PIE_CHART_DATA_MATRIX        : [COLUMNS : [CATEGORY: [LABEL        : "Category",
                                                                                              RESULT_MATRIX: null],
                                                                                   TOTAL   : [LABEL        : "Total %s",
                                                                                              RESULT_MATRIX: FieldFormatType.NUMERIC]],
                                                                        SUFFIXES: [CUSTOMER: "customers",
                                                                                   PRODUCT : "products"]],
                                        ADDITIONAL_DATA_PER_DIMENSION: [PRODUCT : [VOLUME_KPI_KEY],
                                                                        CUSTOMER: []],
                                        MODELS                       : [(CONTRIBUTION_MODEL_KEY): [PP_NAME: "OutliersContributionModelThresholds"]],
                                        INPUTS                       : [PRODUCT             : [LABEL        : "Product(s)",
                                                                                               UNIQUE_KEY   : "outliersPRODUCT",
                                                                                               DEFAULT_VALUE: null],
                                                                        CUSTOMER            : [LABEL        : "Customer(s)",
                                                                                               UNIQUE_KEY   : "outliersCUSTOMER",
                                                                                               DEFAULT_VALUE: null],
                                                                        DATE_FROM           : [LABEL        : "Date From",
                                                                                               UNIQUE_KEY   : "outliersDATE_FROM",
                                                                                               DEFAULT_VALUE: null],
                                                                        DATE_TO             : [LABEL        : "Date To",
                                                                                               UNIQUE_KEY   : "outliersDATE_TO",
                                                                                               DEFAULT_VALUE: null],
                                                                        NO_OF_RESULTS       : [LABEL            : "Top Product(s)/ Customer(s)",
                                                                                               NO_CUSTOMER_LABEL: "Top Product(s)",
                                                                                               UNIQUE_KEY       : "outliersNO_OF_RESULTS",
                                                                                               VALUES           : ["5", "10", "25", "50", "100"],
                                                                                               DEFAULT_VALUE    : "5"],
                                                                        PRODUCT_AGGREGATION : [LABEL               : "Product Aggregation",
                                                                                               UNIQUE_KEY          : "outliersPRODUCT_AGGREGATION",
                                                                                               ENTRY_CATEGORY      : "Product",
                                                                                               ENTRY_CATEGORY_FIELD: "productId"],
                                                                        CUSTOMER_AGGREGATION: [LABEL               : "Customer Aggregation",
                                                                                               UNIQUE_KEY          : "outliersCUSTOMER_AGGREGATION",
                                                                                               ENTRY_CATEGORY      : "Customer",
                                                                                               ENTRY_CATEGORY_FIELD: "customerId"],
                                                                        MODELS              : [LABEL        : "Calculation Model",
                                                                                               UNIQUE_KEY   : "outliersMODELS",
                                                                                               VALUES       : [(MAX_MIN_SPLIT_MODEL_KEY): "(Max - Min) Split",
                                                                                                               (EQUAL_SPLIT_MODEL_KEY)  : "Split Equally",
                                                                                                               (CONTRIBUTION_MODEL_KEY) : "Contribution"],
                                                                                               DEFAULT_VALUE: MAX_MIN_SPLIT_MODEL_KEY],
                                                                        KPI                 : [LABEL     : "KPI",
                                                                                               UNIQUE_KEY: "outliersKPI",
                                                                                               MODELS    : [(MAX_MIN_SPLIT_MODEL_KEY): [VALUES       : [REVENUE_KPI_KEY,
                                                                                                                                                        REVENUE_CONTRIBUTION_PERCENTAGE_KPI_KEY,
                                                                                                                                                        MARGIN_KPI_KEY,
                                                                                                                                                        MARGIN_PERCENTAGE_KPI_KEY,
                                                                                                                                                        MARGIN_CONTRIBUTION_PERCENTAGE_KPI_KEY],
                                                                                                                                        DEFAULT_VALUE: REVENUE_KPI_KEY],
                                                                                                            (EQUAL_SPLIT_MODEL_KEY)  : [VALUES       : [REVENUE_KPI_KEY,
                                                                                                                                                        MARGIN_KPI_KEY],
                                                                                                                                        DEFAULT_VALUE: REVENUE_KPI_KEY],
                                                                                                            (CONTRIBUTION_MODEL_KEY) : [VALUES       : [REVENUE_CONTRIBUTION_PERCENTAGE_KPI_KEY,
                                                                                                                                                        MARGIN_CONTRIBUTION_PERCENTAGE_KPI_KEY],
                                                                                                                                        DEFAULT_VALUE: REVENUE_CONTRIBUTION_PERCENTAGE_KPI_KEY]]],
                                                                        CURRENCY            : [LABEL     : "Currency",
                                                                                               UNIQUE_KEY: "revenueBreakdownCURRENCY"]]]

/**
 * Holds all configuration of the Revenue and Margin Dashboard.
 * The configuration contains
 * - INPUTS - contains all information about available user inputs used in the dashboard. Used to generate default filters.
 */
@Field Map REVENUE_AND_MARGIN_DASHBOARD_CONFIG = [INPUTS: [PRODUCT             : [UNIQUE_KEY   : "revenueAndMarginPRODUCT",
                                                                                  LABEL        : "Product(s)",
                                                                                  DEFAULT_VALUE: null],
                                                           CUSTOMER            : [UNIQUE_KEY   : "revenueAndMarginCUSTOMER",
                                                                                  LABEL        : "Customer(s)",
                                                                                  DEFAULT_VALUE: null],
                                                           DATE_FROM           : [UNIQUE_KEY   : "revenueAndMarginDATE_FROM",
                                                                                  LABEL        : "Date From",
                                                                                  DEFAULT_VALUE: null],
                                                           DATE_TO             : [UNIQUE_KEY   : "revenueAndMarginDATE_TO",
                                                                                  LABEL        : "Date To",
                                                                                  DEFAULT_VALUE: null],
                                                           TIME_PERIOD         : [UNIQUE_KEY   : "revenueAndMarginTIME_PERIOD",
                                                                                  LABEL        : "Time Period",
                                                                                  VALUES       : ["Week", "Month", "Quarter", "Year"],
                                                                                  DEFAULT_VALUE: "Quarter"],
                                                           BAND_BY_CUSTOMER    : [UNIQUE_KEY          : "revenueAndMarginBAND_BY_CUSTOMER",
                                                                                  LABEL               : "Band By For Customer",
                                                                                  ENTRY_CATEGORY      : "Customer",
                                                                                  ENTRY_CATEGORY_FIELD: "customerId"],
                                                           BAND_BY_PRODUCT     : [UNIQUE_KEY          : "revenueAndMarginBAND_BY_PRODUCT",
                                                                                  LABEL               : "Band By For Product",
                                                                                  ENTRY_CATEGORY      : "Product",
                                                                                  ENTRY_CATEGORY_FIELD: "productId"],
                                                           PRODUCT_AGGREGATION : [UNIQUE_KEY          : "revenueAndMarginPRODUCT_AGGREGATION",
                                                                                  LABEL               : "Product Aggregation",
                                                                                  ENTRY_CATEGORY      : "Product",
                                                                                  ENTRY_CATEGORY_FIELD: "productId"],
                                                           CUSTOMER_AGGREGATION: [UNIQUE_KEY          : "revenueAndMarginCUSTOMER_AGGREGATION",
                                                                                  LABEL               : "Customer Aggregation",
                                                                                  ENTRY_CATEGORY      : "Customer",
                                                                                  ENTRY_CATEGORY_FIELD: "customerId"],
                                                           AXIS_TYPE           : [UNIQUE_KEY: "revenueAndMarginAXIS_TYPE",
                                                                                  LABEL     : "Column chart axis type"],
                                                           CURRENCY            : [LABEL     : "Currency",
                                                                                  UNIQUE_KEY: "revenueBreakdownCURRENCY"]]]

/**
 * Defines the key used to describe the world hierarchy in the Regional Revenue and Margin dashboard.
 */
@Field String WORLD_HIERARCHY_KEY = "world"

/**
 * Defines the key used to describe the continent hierarchy in the Regional Revenue and Margin dashboard.
 */
@Field String CONTINENT_HIERARCHY_KEY = "continent"

/**
 * Defines the key used to describe the country hierarchy in the Regional Revenue and Margin dashboard.
 */
@Field String COUNTRY_HIERARCHY_KEY = "country"

/**
 * Defines the key used to describe the region hierarchy in the Regional Revenue and Margin dashboard.
 */
@Field String REGION_HIERARCHY_KEY = "region"

/**
 * Defines the key used to describe the sector hierarchy in the Regional Revenue and Margin dashboard.
 */
@Field String SECTOR_HIERARCHY_KEY = "sector"

/**
 * Holds the configuration for the Regional Revenue and Margin Dashboard configurator
 * Defines a pattern that's used for the world checkbox.
 */
@Field Map REGIONAL_REVENUE_AND_MARGIN_CONFIGURATOR_CONFIG = [NAME  : "Configurator_RegionAndCountry",
                                                              INPUTS: [(WORLD_HIERARCHY_KEY): [PATTERN      : "Display %s map",
                                                                                               DEFAULT_VALUE: true]]]

/**
 * Defines all hierarchies used by the Regional Revenue and Margin Dashboard.
 * Each hierarchy has:
 * - NAME
 * - SQL_FIELD - which points to appropriate key in the DASHBOARDS_ADVANCED_CONFIGURATION_NAME
 * - CONTAINS - which defines the child hierarchy
 */
@Field Map REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG = [(WORLD_HIERARCHY_KEY)    : [NAME     : "World",
                                                                                       SQL_FIELD: null,
                                                                                       CONTAINS : CONTINENT_HIERARCHY_KEY],
                                                           (CONTINENT_HIERARCHY_KEY): [NAME     : "Continent",
                                                                                       SQL_FIELD: "continent",
                                                                                       CONTAINS : COUNTRY_HIERARCHY_KEY],
                                                           (COUNTRY_HIERARCHY_KEY)  : [NAME     : "Country",
                                                                                       SQL_FIELD: "country",
                                                                                       CONTAINS : REGION_HIERARCHY_KEY],
                                                           (REGION_HIERARCHY_KEY)   : [NAME     : "Region",
                                                                                       SQL_FIELD: "region",
                                                                                       CONTAINS : SECTOR_HIERARCHY_KEY],
                                                           (SECTOR_HIERARCHY_KEY)   : [NAME     : "Sector",
                                                                                       SQL_FIELD: "sector",
                                                                                       CONTAINS : null]]

/**
 * Holds information about the SIP_MapHierarchyConfig PP table.
 * The table holds all information about currently used hierarchies.
 * Based on the values setup different hierarchies are enabled or disabled in the Regional Revenue and Margin configurator.
 */
@Field Map REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_PP = [NAME         : "SIP_MapHierarchyConfig",
                                                       TYPE         : "MLTV",
                                                       COLUMN_CONFIG: [HIERARCHY: "name",
                                                                       LABEL    : "attribute1",
                                                                       IS_USED  : "attribute2"],
                                                       IS_USED      : IS_USED_VALUE]

/**
 * Holds information about the SIP_MapCodeOverrides PP table.
 * The table holds all information about the overrides for default ISO_CODES as defined by the user.
 * Used to allow custom names being stored in the DM and not force the ISO_CODE approach.
 */
@Field Map REGIONAL_REVENUE_AND_MARGIN_MAP_CODES_PP = [NAME         : "SIP_MapCodeOverrides",
                                                       TYPE         : "MLTV2",
                                                       COLUMN_CONFIG: [HIERARCHY_LEVEL: "key1",
                                                                       ISO_CODE       : "key2",
                                                                       DM_FIELD_LABEL : "attribute1",
                                                                       DISPLAY_LABEL  : "attribute2"]]

/**
 * Holds information about the SIP_GeoOverrides PP table.
 * The table holds all information about the geoOverrides defined by the user.
 */
@Field Map REGIONAL_REVENUE_AND_MARGIN_GEO_OVERRIDES_PP = [NAME         : "SIP_GeoOverrides",
                                                           TYPE         : "MLTV",
                                                           COLUMN_CONFIG: [ISO_CODE         : "name",
                                                                           PARENT_ISO_CODE  : "attribute1",
                                                                           OVERRIDE_ISO_CODE: "attribute2"]]

/**
 * Holds information about the SIP_Population PP table.
 * The table holds all information about the population of each hierarchy level.
 */
@Field Map REGIONAL_REVENUE_AND_MARGIN_POPULATION_PP = [NAME         : "SIP_Population",
                                                        TYPE         : "MLTV4",
                                                        COLUMN_CONFIG: [(CONTINENT_HIERARCHY_KEY): "key1",
                                                                        (COUNTRY_HIERARCHY_KEY)  : "key2",
                                                                        (REGION_HIERARCHY_KEY)   : "key3",
                                                                        (SECTOR_HIERARCHY_KEY)   : "key4",
                                                                        POPULATION               : "attribute1"],
                                                        IS_USED      : IS_USED_VALUE]
/**
 * Defines the key used to describe the quantity KPI in Regional Revenue and Margin dashboard.
 */
@Field String QUANTITY_KPI_KEY = "quantity"

/**
 * Defines the key used to describe the deviation from average price KPI in Regional Revenue and Margin dashboard.
 */
@Field String DEVIATION_KPI_KEY = "deviationWAP"

/**
 * Defines the key used to describe the revenue per customer KPI in Regional Revenue and Margin dashboard.
 */
@Field String REVENUE_PER_CUSTOMER_KPI_KEY = "revenuePerCustomer"

/**
 * Defines the key used to describe the revenue per X people KPI in Regional Revenue and Margin dashboard.
 */
@Field String REVENUE_PER_POPULATION_KPI_KEY = "revenuePerPopulation"

/**
 * Defines the key used to describe the margin per customer KPI in Regional Revenue and Margin dashboard.
 */
@Field String MARGIN_PER_CUSTOMER_KPI_KEY = "marginPerCustomer"

/**
 * Defines the key used to describe the margin per X people KPI in Regional Revenue and Margin dashboard.
 */
@Field String MARGIN_PER_POPULATION_KPI_KEY = "marginPerPopulation"

/**
 * Holds all configuration of the Regional Revenue and Margin dashboard.
 * The configuration contains
 * - KPI - definition of all KPI used by the dashboard, their formats and rounding
 * - INPUTS - contains all information about available user inputs used in the dashboard. Used to generate default filters.
 * - POPULATION_CALCULATION_CONSTANT - constant user for revenue/margin per X people calculations
 */
@Field Map REGIONAL_REVENUE_AND_MARGIN_DASHBOARD_CONFIG = [KPI                            : [(QUANTITY_KPI_KEY)              : [LABEL   : "Quantity",
                                                                                                                                ROUNDING: 0,
                                                                                                                                PATTERN : "{%s:,.0f}"],
                                                                                             (REVENUE_KPI_KEY)               : [LABEL   : "Revenue",
                                                                                                                                ROUNDING: 0,
                                                                                                                                PATTERN : "{%s:,.0f} %s"],
                                                                                             (MARGIN_KPI_KEY)                : [LABEL   : "Margin",
                                                                                                                                ROUNDING: 0,
                                                                                                                                PATTERN : "{%s:,.0f} %s"],
                                                                                             (MARGIN_PERCENTAGE_KPI_KEY)     : [LABEL   : "Margin %",
                                                                                                                                ROUNDING: 2,
                                                                                                                                PATTERN : "{%s:,.2f} %%"],
                                                                                             (DEVIATION_KPI_KEY)             : [LABEL   : "Deviation from weighted average price",
                                                                                                                                ROUNDING: 6,
                                                                                                                                PATTERN : "{%s:,.2f} %s"],
                                                                                             (REVENUE_PER_CUSTOMER_KPI_KEY)  : [LABEL                 : "Revenue per customer",
                                                                                                                                ROUNDING              : 0,
                                                                                                                                PATTERN               : "{%s:,.0f} %s",
                                                                                                                                REQUIRES_CUSTOMER_DATA: true],
                                                                                             (MARGIN_PER_CUSTOMER_KPI_KEY)   : [LABEL                 : "Margin per customer",
                                                                                                                                ROUNDING              : 0,
                                                                                                                                PATTERN               : "{%s:,.0f} %s",
                                                                                                                                REQUIRES_CUSTOMER_DATA: true],
                                                                                             (REVENUE_PER_POPULATION_KPI_KEY): [LABEL   : "Revenue per 1000 people",
                                                                                                                                ROUNDING: 0,
                                                                                                                                PATTERN : "{%s:,.0f} %s"],
                                                                                             (MARGIN_PER_POPULATION_KPI_KEY) : [LABEL   : "Margin per 1000 people",
                                                                                                                                ROUNDING: 0,
                                                                                                                                PATTERN : "{%s:,.0f} %s"]],
                                                           INPUTS                         : [PRODUCT  : [LABEL        : "Product(s)",
                                                                                                         UNIQUE_KEY   : "regionalPRODUCT",
                                                                                                         DEFAULT_VALUE: null],
                                                                                             CUSTOMER : [LABEL        : "Customer(s)",
                                                                                                         UNIQUE_KEY   : "regionalCUSTOMER",
                                                                                                         DEFAULT_VALUE: null],
                                                                                             DATE_FROM: [LABEL        : "Date From",
                                                                                                         UNIQUE_KEY   : "regionalDATE_FROM",
                                                                                                         DEFAULT_VALUE: null],
                                                                                             DATE_TO  : [LABEL        : "Date To",
                                                                                                         UNIQUE_KEY   : "regionalDATE_TO",
                                                                                                         DEFAULT_VALUE: null],
                                                                                             KPI      : [LABEL        : "KPI",
                                                                                                         UNIQUE_KEY   : "regionalKPI",
                                                                                                         VALUES       : [QUANTITY_KPI_KEY,
                                                                                                                         REVENUE_KPI_KEY,
                                                                                                                         MARGIN_KPI_KEY,
                                                                                                                         MARGIN_PERCENTAGE_KPI_KEY,
                                                                                                                         DEVIATION_KPI_KEY,
                                                                                                                         REVENUE_PER_CUSTOMER_KPI_KEY,
                                                                                                                         MARGIN_PER_CUSTOMER_KPI_KEY,
                                                                                                                         REVENUE_PER_POPULATION_KPI_KEY,
                                                                                                                         MARGIN_PER_POPULATION_KPI_KEY],
                                                                                                         DEFAULT_VALUE: REVENUE_KPI_KEY],
                                                                                             CURRENCY : [LABEL     : "Currency",
                                                                                                         UNIQUE_KEY: "revenueBreakdownCURRENCY"]],
                                                           POPULATION_CALCULATION_CONSTANT: 1000]

/**
 * Holds all configuration that is common for both the Waterfall and Comparison Waterfall dashboards.
 * The configuration contains
 * - KPI - definition of all KPI used by the dashboard, their formats and rounding
 * - INPUTS - contains all information about available user inputs used in the dashboard. Used to generate default filters.
 * - POPULATION_CALCULATION_CONSTANT - constant user for revenue/margin per X people calculations
 */
@Field Map COMMON_WATERFALL_DASHBOARDS_CONFIG = [WATERFALL_COLUMN_COLORS: [BASE     : [PRICE   : "#0080FF",
                                                                                       NEGATIVE: "#BF4040",
                                                                                       POSITIVE: "#00FF00"],
                                                                           SECONDARY: [PRICE   : "#1f2a8d",
                                                                                       NEGATIVE: "#dd0074",
                                                                                       POSITIVE: "#99da00"]]]

/**
 * Defines the key used to describe the absolute calculation model in Waterfall and Waterfall Comparison Dashboard.
 */
@Field String WATERFALL_MODEL_ABSOLUTE_NAME = "absolute"

/**
 * Defines the key used to describe the percentage calculation model in Waterfall and Waterfall Comparison Dashboard.
 */
@Field String WATERFALL_MODEL_PERCENTAGE_NAME = "percentage"

/**
 * Defines the key used to describe the detail calculation model in Waterfall Dashboard.
 */
@Field String WATERFALL_MODEL_DETAIL_NAME = "detail"

/**
 * Defines the key used to describe the absoluteUnit calculation model in Waterfall and Waterfall Comparison Dashboard.
 */
@Field String WATERFALL_MODEL_ABSOLUTE_UNIT_NAME = "absoluteUnit"

/**
 * Holds all configuration of the Waterfall dashboard.
 * The configuration contains
 * - MODELS - definition of all calculation models supported by the dashboard and their additional configuration (like titles)
 * - INPUTS - contains all information about available user inputs used in the dashboard. Used to generate default filters.
 */
@Field Map WATERFALL_DASHBOARD_CONFIG = [MODELS: [(WATERFALL_MODEL_ABSOLUTE_NAME)     : [LABEL: "Absolute",
                                                                                         TITLE: "Waterfall With Absolute Value"],
                                                  (WATERFALL_MODEL_DETAIL_NAME)       : [LABEL: "Absolute Detail",
                                                                                         TITLE: "Waterfall With Absolute Detail Value"],
                                                  (WATERFALL_MODEL_ABSOLUTE_UNIT_NAME): [LABEL: "By Absolute Unit",
                                                                                         TITLE: "Waterfall By Absolute Unit Value"],
                                                  (WATERFALL_MODEL_PERCENTAGE_NAME)   : [LABEL: "Percentage",
                                                                                         TITLE: "Waterfall With Percentage Value"]],
                                         INPUTS: [PRODUCT  : [LABEL        : "Product(s)",
                                                              UNIQUE_KEY   : "waterfallPRODUCT",
                                                              DEFAULT_VALUE: null],
                                                  CUSTOMER : [LABEL        : "Customer(s)",
                                                              UNIQUE_KEY   : "waterfallCUSTOMER",
                                                              DEFAULT_VALUE: null],
                                                  DATE_FROM: [LABEL        : "Date From",
                                                              UNIQUE_KEY   : "waterfallDATE_FROM",
                                                              DEFAULT_VALUE: null],
                                                  DATE_TO  : [LABEL        : "Date To",
                                                              UNIQUE_KEY   : "waterfallDATE_TO",
                                                              DEFAULT_VALUE: null],
                                                  MODELS   : [LABEL        : "Waterfall Model",
                                                              UNIQUE_KEY   : "waterfallMODELS",
                                                              DEFAULT_VALUE: WATERFALL_MODEL_ABSOLUTE_NAME],
                                                  CURRENCY : [LABEL     : "Currency",
                                                              UNIQUE_KEY: "revenueBreakdownCURRENCY"]]]

/**
 * Defines the key used to describe the product comparison dimension in Waterfall Comparison Dashboard.
 */
@Field String COMPARISON_DIMENSION_PRODUCT_KEY = "product"

/**
 * Defines the key used to describe the customer comparison dimension in Waterfall Comparison Dashboard.
 */
@Field String COMPARISON_DIMENSION_CUSTOMER_KEY = "customer"

/**
 * Defines the key used to describe the date comparison dimension in Waterfall Comparison Dashboard.
 */
@Field String COMPARISON_DIMENSION_DATE_KEY = "date"

/**
 * Holds all configuration of the Waterfall Comparison dashboard.
 * The configuration contains
 * - MODELS - definition of all calculation models supported by the dashboard and their additional configuration (like titles)
 * - COMPARISON_DIMENSIONS - definition of all comparison dimensions supported by the dashboard and their additional configuration (like titles)
 * - INPUTS - contains all information about available user inputs used in the dashboard. Used to generate default filters.
 */
@Field Map WATERFALL_COMPARISON_DASHBOARD_CONFIG = [MODELS               : [(WATERFALL_MODEL_ABSOLUTE_NAME)     : [LABEL: "Absolute",
                                                                                                                   TITLE: "Absolute Value"],
                                                                            (WATERFALL_MODEL_ABSOLUTE_UNIT_NAME): [LABEL: "By Absolute Unit",
                                                                                                                   TITLE: "By Absolute Unit Value"],
                                                                            (WATERFALL_MODEL_PERCENTAGE_NAME)   : [LABEL: "Percentage",
                                                                                                                   TITLE: "Percentage Value"]],
                                                    COMPARISON_DIMENSIONS: [(COMPARISON_DIMENSION_PRODUCT_KEY) : [LABEL: "Product",
                                                                                                                  TITLE: "Product(s)"],
                                                                            (COMPARISON_DIMENSION_CUSTOMER_KEY): [LABEL: "Customer",
                                                                                                                  TITLE: "Customer(s)"],
                                                                            (COMPARISON_DIMENSION_DATE_KEY)    : [LABEL: "Date",
                                                                                                                  TITLE: "Date"]],
                                                    INPUTS               : [PRODUCT              : [LABEL        : "Product(s)",
                                                                                                    UNIQUE_KEY   : "waterfallComparisonPRODUCT",
                                                                                                    SUFFIXES     : [FIRST : " (1)",
                                                                                                                    SECOND: " (2)"],
                                                                                                    DEFAULT_VALUE: null],
                                                                            CUSTOMER             : [LABEL        : "Customer(s)",
                                                                                                    UNIQUE_KEY   : "waterfallComparisonCUSTOMER",
                                                                                                    SUFFIXES     : [FIRST : " (1)",
                                                                                                                    SECOND: " (2)"],
                                                                                                    DEFAULT_VALUE: null],
                                                                            DATE_FROM            : [LABEL        : "Date From",
                                                                                                    UNIQUE_KEY   : "waterfallComparisonDATE_FROM",
                                                                                                    SUFFIXES     : [FIRST : " (1)",
                                                                                                                    SECOND: " (2)"],
                                                                                                    DEFAULT_VALUE: [FIRST : [YEAR_ADJUSTMENT: -1,
                                                                                                                             MONTH          : 0,
                                                                                                                             DAY            : 1],
                                                                                                                    SECOND: [YEAR_ADJUSTMENT: -2,
                                                                                                                             MONTH          : 0,
                                                                                                                             DAY            : 1]]],
                                                                            DATE_TO              : [LABEL        : "Date To",
                                                                                                    UNIQUE_KEY   : "waterfallComparisonDATE_TO",
                                                                                                    SUFFIXES     : [FIRST : " (1)",
                                                                                                                    SECOND: " (2)"],
                                                                                                    DEFAULT_VALUE: [FIRST : [YEAR_ADJUSTMENT: -1,
                                                                                                                             MONTH          : 11,
                                                                                                                             DAY            : 31],
                                                                                                                    SECOND: [YEAR_ADJUSTMENT: -2,
                                                                                                                             MONTH          : 11,
                                                                                                                             DAY            : 31]]],
                                                                            COMPARISON_DIMENSIONS: [LABEL        : "Comparison Type",
                                                                                                    UNIQUE_KEY   : "waterfallComparisonCOMPARISON_DIMENSIONS",
                                                                                                    DEFAULT_VALUE: COMPARISON_DIMENSION_DATE_KEY],
                                                                            MODELS               : [LABEL        : "Waterfall Model",
                                                                                                    UNIQUE_KEY   : "waterfallComparisonMODELS",
                                                                                                    DEFAULT_VALUE: WATERFALL_MODEL_ABSOLUTE_NAME],
                                                                            CURRENCY             : [LABEL     : "Currency",
                                                                                                    UNIQUE_KEY: "revenueBreakdownCURRENCY"]]]

/**
 * Defines a year format used by various dashboards
 */
@Field String YEAR_FORMAT = "yyyy"

/**
 * Defines the name of the System generated prefix for Year column in DM, generated by setting the column function as PricingDate
 */
@Field String YEAR_FIELD_SUFFIX = "Year"

/**
 * Defines the YTD value used to describe the period type used in Causality Dashboards.
 */
@Field String YTD_PERIOD_VALUE = "YTD"

/**
 * Defines the Day value used to describe the period type used in Causality / PoP Dashboards.
 */
@Field String DAY_PERIOD_VALUE = "Day"

/**
 * Defines the Week value used to describe the period type used in Causality / PoP Dashboards.
 */
@Field String WEEK_PERIOD_VALUE = "Week"

/**
 * Defines the Month value used to describe the period type used in Causality / PoP Dashboards.
 */
@Field String MONTH_PERIOD_VALUE = "Month"

/**
 * Defines the QuadWeek value used to describe the period type used in Causality / PoP Dashboards.
 */
@Field String QUAD_WEEK_PERIOD_VALUE = "QuadWeek"

/**
 * Defines the Quarter value used to describe the period type used in Causality / PoP Dashboards.
 */
@Field String QUARTER_PERIOD_VALUE = "Quarter"

/**
 * Defines the Year value used to describe the period type used in Causality / PoP Dashboards.
 */
@Field String YEAR_PERIOD_VALUE = "Year"

/**
 * Defines the Custom value used to describe the period type used in Causality Dashboards.
 */
@Field String CUSTOM_PERIOD_VALUE = "Custom"

/**
 * Holds all configuration of the Revenue Breakdown dashboard.
 * The configuration contains
 * - INPUTS - contains all information about available user inputs used in the dashboard. Used to generate default filters.
 */
@Field Map REVENUE_BREAKDOWN_DASHBOARD_CONFIG = [INPUTS: [PRODUCT             : [LABEL        : "Product(s)",
                                                                                 UNIQUE_KEY   : "revenueBreakdownPRODUCT",
                                                                                 DEFAULT_VALUE: null],
                                                          PRODUCT_AGGREGATION : [LABEL               : "Product Aggregation",
                                                                                 UNIQUE_KEY          : "revenueBreakdownPRODUCT_AGGREGATION",
                                                                                 ENTRY_CATEGORY      : "Product",
                                                                                 ENTRY_CATEGORY_FIELD: "productId"],
                                                          CUSTOMER            : [LABEL        : "Customer(s)",
                                                                                 UNIQUE_KEY   : "revenueBreakdownCUSTOMER",
                                                                                 DEFAULT_VALUE: null],
                                                          CUSTOMER_AGGREGATION: [LABEL               : "Customer Aggregation",
                                                                                 UNIQUE_KEY          : "revenueBreakdownCUSTOMER_AGGREGATION",
                                                                                 ENTRY_CATEGORY      : "Customer",
                                                                                 ENTRY_CATEGORY_FIELD: "customerId"],
                                                          YEAR                : [LABEL        : "Year",
                                                                                 UNIQUE_KEY   : "revenueBreakdownYEAR",
                                                                                 FIELD_SUFFIX : YEAR_FIELD_SUFFIX,
                                                                                 FORMAT       : YEAR_FORMAT,
                                                                                 DEFAULT_VALUE: {
                                                                                     return libs.SharedLib.DateUtils.currentYear()
                                                                                 }],
                                                          COMPARISON_YEAR     : [LABEL        : "Comparison Year",
                                                                                 UNIQUE_KEY   : "revenueBreakdownCOMPARISON_YEAR",
                                                                                 DATE         : [FORMAT: "yyyy-MM-dd"],
                                                                                 FIELD_SUFFIX : YEAR_FIELD_SUFFIX,
                                                                                 FORMAT       : YEAR_FORMAT,
                                                                                 DEFAULT_VALUE: {
                                                                                     return libs.SharedLib.DateUtils.currentYear() - 1
                                                                                 }],
                                                          PERIOD              : [LABEL        : "Quarter",
                                                                                 UNIQUE_KEY   : "revenueBreakdownPERIOD",
                                                                                 VALUES       : ["Q1", "Q2", "Q3", "Q4", EMPTY_DIMENSION_VALUE],
                                                                                 DEFAULT_VALUE: {
                                                                                     return libs.SIP_Dashboards_Commons.InputUtils.getCurrentQuarter()
                                                                                 }],
                                                          COMPARISON_PERIOD   : [LABEL        : "Comparison Quarter",
                                                                                 UNIQUE_KEY   : "revenueBreakdownCOMPARISON_PERIOD",
                                                                                 VALUES       : ["Q1", "Q2", "Q3", "Q4", EMPTY_DIMENSION_VALUE],
                                                                                 DEFAULT_VALUE: {
                                                                                     return libs.SIP_Dashboards_Commons.InputUtils.getCurrentQuarter()
                                                                                 }],
                                                          SHOW_AS_PERCENTAGE  : [LABEL        : "Show Percentage (%)",
                                                                                 UNIQUE_KEY   : "revenueBreakdownSHOW_AS_PERCENTAGE",
                                                                                 DEFAULT_VALUE: false],
                                                          CURRENCY            : [LABEL     : "Currency",
                                                                                 UNIQUE_KEY: "revenueBreakdownCURRENCY"]]]

@Field Map CAUSALITY_PERIOD_CONFIGURATOR_CONFIG = [LOGIC_NAME: "SIP_Causality_Period_Configurator",
                                                   INPUTS    : [YEAR                    : [LABEL        : "Year",
                                                                                           UNIQUE_KEY   : "causalityPeriodYEAR",
                                                                                           FIELD_SUFFIX : YEAR_FIELD_SUFFIX,
                                                                                           FORMAT       : YEAR_FORMAT,
                                                                                           DEFAULT_VALUE: {
                                                                                               return libs.SharedLib.DateUtils.currentYear()
                                                                                           }],
                                                                COMPARISON_YEAR         : [LABEL        : "Comparison Year",
                                                                                           UNIQUE_KEY   : "causalityPeriodCOMPARISON_YEAR",
                                                                                           FIELD_SUFFIX : YEAR_FIELD_SUFFIX,
                                                                                           FORMAT       : YEAR_FORMAT,
                                                                                           DEFAULT_VALUE: {
                                                                                               return libs.SharedLib.DateUtils.currentYear() - 1
                                                                                           }],
                                                                PERIOD_TYPE             : [LABEL        : "Period Type",
                                                                                           UNIQUE_KEY   : "causalityPeriodPERIOD_TYPE",
                                                                                           VALUES       : [WEEK_PERIOD_VALUE, MONTH_PERIOD_VALUE, QUARTER_PERIOD_VALUE, YTD_PERIOD_VALUE, CUSTOM_PERIOD_VALUE],
                                                                                           DEFAULT_VALUE: {
                                                                                               return QUARTER_PERIOD_VALUE
                                                                                           }],
                                                                PERIOD                  : [UNIQUE_KEY            : "causalityPeriodPERIOD",
                                                                                           (QUARTER_PERIOD_VALUE): [LABEL        : ["Quarter"],
                                                                                                                    DEFAULT_VALUE: {
                                                                                                                        return libs.SIP_Dashboards_Commons.InputUtils.getCurrentQuarter()
                                                                                                                    }],
                                                                                           (WEEK_PERIOD_VALUE)   : [LABEL        : ["Week"],
                                                                                                                    DEFAULT_VALUE: {
                                                                                                                        return libs.SIP_Dashboards_Commons.InputUtils.getCurrentWeek()
                                                                                                                    }],
                                                                                           (MONTH_PERIOD_VALUE)  : [LABEL        : ["Month"],
                                                                                                                    DEFAULT_VALUE: {
                                                                                                                        return libs.SIP_Dashboards_Commons.InputUtils.getCurrentMonth()
                                                                                                                    }]],
                                                                COMPARISON_PERIOD       : [UNIQUE_KEY            : "causalityComparisonPeriodPERIOD",
                                                                                           (QUARTER_PERIOD_VALUE): [LABEL        : ["Comparison Quarter"],
                                                                                                                    DEFAULT_VALUE: {
                                                                                                                        return libs.SIP_Dashboards_Commons.InputUtils.getCurrentQuarter()
                                                                                                                    }],
                                                                                           (WEEK_PERIOD_VALUE)   : [LABEL        : ["Comparison Week"],
                                                                                                                    DEFAULT_VALUE: {
                                                                                                                        return libs.SIP_Dashboards_Commons.InputUtils.getCurrentWeek()
                                                                                                                    }],
                                                                                           (MONTH_PERIOD_VALUE)  : [LABEL        : ["Comparison Month"],
                                                                                                                    DEFAULT_VALUE: {
                                                                                                                        return libs.SIP_Dashboards_Commons.InputUtils.getCurrentMonth()
                                                                                                                    }]],
                                                                DATE_FROM               : [LABEL        : "Date From",
                                                                                           UNIQUE_KEY   : "causalityPeriodDATE_FROM",
                                                                                           DEFAULT_VALUE: [YEAR_ADJUSTMENT: 0,
                                                                                                           MONTH          : 0,
                                                                                                           DAY            : 1]],
                                                                DATE_TO                 : [LABEL        : "Date To",
                                                                                           UNIQUE_KEY   : "causalityPeriodDATE_TO",
                                                                                           DEFAULT_VALUE: [YEAR_ADJUSTMENT: 0,
                                                                                                           MONTH          : null,
                                                                                                           DAY            : null]],
                                                                COMPARISON_DATE_FROM    : [LABEL        : "Comparison Date From",
                                                                                           UNIQUE_KEY   : "causalityComparisonPeriodDATE_FROM",
                                                                                           DEFAULT_VALUE: [YEAR_ADJUSTMENT: -1,
                                                                                                           MONTH          : 0,
                                                                                                           DAY            : 1]],
                                                                COMPARISON_DATE_TO      : [LABEL        : "Comparison Date To",
                                                                                           UNIQUE_KEY   : "causalityComparisonPeriodDATE_TO",
                                                                                           DEFAULT_VALUE: [YEAR_ADJUSTMENT: -1,
                                                                                                           MONTH          : null,
                                                                                                           DAY            : null]],
                                                                PERIOD_OUTPUT           : [UNIQUE_KEY: "causalityPeriodPERIOD_OUTPUT"],
                                                                COMPARISON_PERIOD_OUTPUT: [UNIQUE_KEY: "causalityPeriodCOMPARISON_PERIOD_OUTPUT"]]]

/**
 * Defines the data for Final interval that used in PeriodOverPeriod dashboard
 */
@Field Map PERIOD_OVER_PERIOD_FINAL_INTERVAL_OPTIONS = [LATEST: "Latest Whole Interval",
                                                        X_AGO : "X Whole Intervals Ago",
                                                        MANUAL: "Manual Entry"]
/**
 * Defines the period types
 */
@Field final Map PERIOD_OVER_PERIOD_PERIOD_TYPES = [DAY      : [CODE: "D",
                                                                TEXT: "Day"],
                                                    WEEK     : [CODE: "W",
                                                                TEXT: "Week"],
                                                    QUAD_WEEK: [CODE: "QW",
                                                                TEXT: "QuadWeek"],
                                                    MONTH    : [CODE: "M",
                                                                TEXT: "Month"],
                                                    QUARTER  : [CODE: "Q",
                                                                TEXT: "Quarter"],
                                                    YEAR     : [CODE: "Y",
                                                                TEXT: "Year"]]

/**
 * Defines the measure aggregation
 */
@Field final String PERIOD_OVER_PERIOD_MEASURE_AGGREGATION = "SUM"

/**
 * Defines the first day of week
 */
@Field final String PERIOD_OVER_PERIOD_DAY_OF_WEEK_MONDAY = "Monday"

/**
 * Defines the PeriodOverPeriod dashboard config
 */
@Field Map PERIOD_OVER_PERIOD_CONFIGURATOR_CONFIG = [LOGIC_NAME                   : "Dashboard_PeriodOverPeriod_Configurator",
                                                     INPUTS                       : [CUSTOMER_GROUP              : [NAME : "CustomerGroup",
                                                                                                                    LABEL: "Customer(s)"],
                                                                                     PRODUCT_GROUP               : [NAME : "ProductGroup",
                                                                                                                    LABEL: "Product(s)"],
                                                                                     FINAL_INTERVAL              : [NAME : "FinalInterval",
                                                                                                                    LABEL: "Final Interval"],
                                                                                     FINAL_INTERVAL_X            : [NAME            : "FinalIntervalX",
                                                                                                                    LABEL           : "Final Interval: ",
                                                                                                                    INPUT_TYPES     : [(PERIOD_OVER_PERIOD_FINAL_INTERVAL_OPTIONS.LATEST): null,
                                                                                                                                       (PERIOD_OVER_PERIOD_FINAL_INTERVAL_OPTIONS.X_AGO) : InputType.INTEGERUSERENTRY,
                                                                                                                                       (PERIOD_OVER_PERIOD_FINAL_INTERVAL_OPTIONS.MANUAL): InputType.STRINGUSERENTRY],
                                                                                                                    MANUAL_INFO_TEXT: "Provide exact period value according to the selected Interval Size. E.g. “2022-W10”, ”2021-Q1”, ”2020-QW3”."],
                                                                                     INTERVAL_SIZE               : [NAME   : "IntervalSize",
                                                                                                                    LABEL  : "Interval Size",
                                                                                                                    DEFAULT: MONTH_PERIOD_VALUE],
                                                                                     MEASURE_COLUMN              : [NAME : "MeasureColumn",
                                                                                                                    LABEL: "Measure Column"],
                                                                                     MEASURE_TYPE                : [NAME : "MeasureType",
                                                                                                                    LABEL: "Measure Type"],
                                                                                     DISPLAYED_INTERVALS         : [NAME   : "DisplayIntervals",
                                                                                                                    LABEL  : "Number of Intervals",
                                                                                                                    DEFAULT: 24],
                                                                                     RATIO_DENOMINATOR           : [NAME        : "RatioDenominator",
                                                                                                                    LABEL       : "Denominator",
                                                                                                                    FIELD_SUFFIX: "Column"],
                                                                                     RATIO_NUMERATOR             : [NAME        : "RatioNumerator",
                                                                                                                    LABEL       : "Numerator",
                                                                                                                    FIELD_SUFFIX: "Column"],
                                                                                     RATIO_TYPE                  : [NAME : "RatioType",
                                                                                                                    LABEL: "Ratio Type"],
                                                                                     SCALE_CHANGE_BARS_PERCENT   : [NAME : "ScaleChangeBarsAsPercent",
                                                                                                                    LABEL: "Scale Change Bars as %"],
                                                                                     Z_AXIS                      : [NAME : "DisplayZAxis",
                                                                                                                    LABEL: "Display Z Axis"],
                                                                                     TRAILING_INTERVALS          : [NAME   : "TrailingDistance",
                                                                                                                    LABEL  : "Trailing Offset By X ...",
                                                                                                                    PATTERN: "Offset of comparison period in %ss",
                                                                                                                    DEFAULT: 12],
                                                                                     IMPACT_PERIOD               : [NAME : "ImpactPeriod",
                                                                                                                    LABEL: "Include Impact Period"],
                                                                                     FIRST_IMPACT_PERIOD         : [NAME   : "FinalImpactPeriod",
                                                                                                                    LABEL  : "Final Impact ...",
                                                                                                                    PATTERN: "Final Impact %s"],
                                                                                     DATE_TO                     : [NAME : "DateTo",
                                                                                                                    LABEL: "Date To"],
                                                                                     LENGTH_OF_IMPACT_PERIOD     : [NAME   : "LengthOfImpactPeriod",
                                                                                                                    LABEL  : "Length of Impact Period",
                                                                                                                    DEFAULT: 1],
                                                                                     CURRENCY                    : [NAME : "UserCurrency",
                                                                                                                    LABEL: "Currency"],
                                                                                     GENERAL_FILTER              : [NAME : "GeneralFilter",
                                                                                                                    LABEL: "General Filter"],
                                                                                     INPUT_CHANGE_DETECTION_CACHE: [NAME: "InputChangeDetectionCache"]],
                                                     MEASURE_TYPES                : [SINGLE: "Single Column",
                                                                                     RATIO : "Ratio"],
                                                     PERIOD_DELIMITER             : "-",
                                                     QUERY_CONFIG                 : [MEASURE_COLUMN_LABEL: "Measure",
                                                                                     SQL_SOURCE_ALIAS    : "source"],
                                                     AGGREGATION_OPTIONS          : ["SUM", "AVG"],
                                                     INTERVAL_OPTIONS             : [DAY      : [VALUE: DAY_PERIOD_VALUE, LABEL: DAY_PERIOD_VALUE, DEFAULT_SIZE: 365],
                                                                                     WEEK     : [VALUE: WEEK_PERIOD_VALUE, LABEL: WEEK_PERIOD_VALUE, DEFAULT_SIZE: 52],
                                                                                     QUAD_WEEK: [VALUE: QUAD_WEEK_PERIOD_VALUE, LABEL: QUAD_WEEK_PERIOD_VALUE, DEFAULT_SIZE: 13],
                                                                                     MONTH    : [VALUE: MONTH_PERIOD_VALUE, LABEL: MONTH_PERIOD_VALUE, DEFAULT_SIZE: 12],
                                                                                     QUARTER  : [VALUE: QUARTER_PERIOD_VALUE, LABEL: QUARTER_PERIOD_VALUE, DEFAULT_SIZE: 4],
                                                                                     YEAR     : [VALUE: YEAR_PERIOD_VALUE, LABEL: YEAR_PERIOD_VALUE, DEFAULT_SIZE: 1]],
                                                     CONVERSION_COEFFICIENT_MATRIX: [(DAY_PERIOD_VALUE)      : [(DAY_PERIOD_VALUE): 1, (WEEK_PERIOD_VALUE): 1 / 7, (QUAD_WEEK_PERIOD_VALUE): 1 / 28, (MONTH_PERIOD_VALUE): 1 / 30, (QUARTER_PERIOD_VALUE): 1 / 90, (YEAR_PERIOD_VALUE): 1 / 365],
                                                                                     (WEEK_PERIOD_VALUE)     : [(DAY_PERIOD_VALUE): 7, (WEEK_PERIOD_VALUE): 1, (QUAD_WEEK_PERIOD_VALUE): 1 / 4, (MONTH_PERIOD_VALUE): 1 / 4, (QUARTER_PERIOD_VALUE): 1 / 13, (YEAR_PERIOD_VALUE): 1 / 52],
                                                                                     (QUAD_WEEK_PERIOD_VALUE): [(DAY_PERIOD_VALUE): 28, (WEEK_PERIOD_VALUE): 4, (QUAD_WEEK_PERIOD_VALUE): 1, (MONTH_PERIOD_VALUE): 1, (QUARTER_PERIOD_VALUE): 1 / 3, (YEAR_PERIOD_VALUE): 1 / 13],
                                                                                     (MONTH_PERIOD_VALUE)    : [(DAY_PERIOD_VALUE): 30, (WEEK_PERIOD_VALUE): 4, (QUAD_WEEK_PERIOD_VALUE): 1, (MONTH_PERIOD_VALUE): 1, (QUARTER_PERIOD_VALUE): 1 / 3, (YEAR_PERIOD_VALUE): 1 / 12],
                                                                                     (QUARTER_PERIOD_VALUE)  : [(DAY_PERIOD_VALUE): 90, (WEEK_PERIOD_VALUE): 13, (QUAD_WEEK_PERIOD_VALUE): 3, (MONTH_PERIOD_VALUE): 3, (QUARTER_PERIOD_VALUE): 1, (YEAR_PERIOD_VALUE): 1 / 4],
                                                                                     (YEAR_PERIOD_VALUE)     : [(DAY_PERIOD_VALUE): 365, (WEEK_PERIOD_VALUE): 52, (QUAD_WEEK_PERIOD_VALUE): 13, (MONTH_PERIOD_VALUE): 12, (QUARTER_PERIOD_VALUE): 4, (YEAR_PERIOD_VALUE): 1]]]


@Field Map RATIO_TYPES = [PERCENTAGE_BASED: [GROSS_MARGIN     : "Gross Margin %",
                                             PRICE_LEAKAGE    : "Price Leakage %",
                                             PRICE_REALIZATION: "Price Realization %",
                                             INCENTIVE        : "Incentive %"],
                          ABSOLUTE_BASED  : [AVERAGE_PRICE : "Average Price Per Unit",
                                             AVERAGE_PROFIT: "Average Profit Per Unit"],
                          CUSTOM          : [CUSTOM: "Custom"]]

@Field Map RATIO_TYPE_OPTIONS = [(RATIO_TYPES.PERCENTAGE_BASED.GROSS_MARGIN)     : [NUMERATOR_LABEL   : "Gross Margin",
                                                                                    NUMERATOR_COLUMN  : "grossMargin",
                                                                                    DENOMINATOR_LABEL : "Invoice Price",
                                                                                    DENOMINATOR_COLUMN: "invoicePrice",],
                                 (RATIO_TYPES.PERCENTAGE_BASED.PRICE_LEAKAGE)    : [NUMERATOR_LABEL   : "Net Price",
                                                                                    NUMERATOR_COLUMN  : "netPrice",
                                                                                    DENOMINATOR_LABEL : "Local List Price",
                                                                                    DENOMINATOR_COLUMN: "localListPrice",],
                                 (RATIO_TYPES.PERCENTAGE_BASED.PRICE_REALIZATION): [NUMERATOR_LABEL   : "Invoice Price",
                                                                                    NUMERATOR_COLUMN  : "invoicePrice",
                                                                                    DENOMINATOR_LABEL : "Global List Price",
                                                                                    DENOMINATOR_COLUMN: "globalListPrice",],
                                 (RATIO_TYPES.PERCENTAGE_BASED.INCENTIVE)        : [NUMERATOR_LABEL   : "Net Sales",
                                                                                    NUMERATOR_COLUMN  : "", // There is no default value
                                                                                    DENOMINATOR_LABEL : "Local List Price",
                                                                                    DENOMINATOR_COLUMN: "", // There is no default value
                                 ],
                                 (RATIO_TYPES.ABSOLUTE_BASED.AVERAGE_PRICE)      : [NUMERATOR_LABEL   : "Invoice Price",
                                                                                    NUMERATOR_COLUMN  : "invoicePrice",
                                                                                    DENOMINATOR_LABEL : "Quantity",
                                                                                    DENOMINATOR_COLUMN: "quantity",],
                                 (RATIO_TYPES.ABSOLUTE_BASED.AVERAGE_PROFIT)     : [NUMERATOR_LABEL   : "Gross Margin",
                                                                                    NUMERATOR_COLUMN  : "grossMargin",
                                                                                    DENOMINATOR_LABEL : "Quantity",
                                                                                    DENOMINATOR_COLUMN: "quantity",],
                                 (RATIO_TYPES.CUSTOM.CUSTOM)                     : [NUMERATOR_LABEL   : "Numerator",
                                                                                    NUMERATOR_COLUMN  : "", // There is no default value
                                                                                    DENOMINATOR_LABEL : "Denominator",
                                                                                    DENOMINATOR_COLUMN: "", // There is no default value
                                 ]]

/**
 * Defines the key used to describe the net calculation model used in the Margin Breakdown Dashboard.
 */
@Field String MARGIN_BREAKDOWN_NET_CHART_KEY = "net"

/**
 * Defines the key used to describe the gross calculation model used in the Margin Breakdown Dashboard.
 */
@Field String MARGIN_BREAKDOWN_GROSS_CHART_KEY = "gross"

/**
 * Defines the key used to describe the averages calculation model used in the Margin Breakdown Dashboard.
 */
@Field String MARGIN_BREAKDOWN_AVERAGES_CHART_KEY = "averages"

/**
 * Defines the key used to describe the most used calculation model used in the Margin Breakdown Dashboard.
 */
@Field String MARGIN_BREAKDOWN_MOST_USED_CHART_KEY = "mostUsed"

/**
 * Holds all configuration of the Margin Breakdown dashboard.
 * The configuration contains
 * - MODELS - definition of all calculation models supported by the dashboard and their additional configuration (like titles)
 * - INPUTS - contains all information about available user inputs used in the dashboard. Used to generate default filters.
 */
@Field Map MARGIN_BREAKDOWN_DASHBOARD_CONFIG = [MODELS                   : [(MARGIN_BREAKDOWN_NET_CHART_KEY)      : [LABEL: "Net",
                                                                                                                     TITLE: "Net Margin Breakdown"],
                                                                            (MARGIN_BREAKDOWN_GROSS_CHART_KEY)    : [LABEL: "Gross",
                                                                                                                     TITLE: "Gross Margin Breakdown"],
                                                                            (MARGIN_BREAKDOWN_AVERAGES_CHART_KEY) : [LABEL: "Averages",
                                                                                                                     TITLE: "Averages Margin Breakdown"],
                                                                            (MARGIN_BREAKDOWN_MOST_USED_CHART_KEY): [LABEL: "Most Used",
                                                                                                                     TITLE: "Most Used Margin Breakdown"]],
                                                STANDARD_MODE_CHART_TITLE: "Margin Breakdown",
                                                INPUTS                   : [PRODUCT             : [LABEL        : "Product(s)",
                                                                                                   UNIQUE_KEY   : "marginBreakdownPRODUCT",
                                                                                                   DEFAULT_VALUE: null],
                                                                            PRODUCT_AGGREGATION : [LABEL               : "Product Aggregation",
                                                                                                   UNIQUE_KEY          : "marginBreakdownPRODUCT_AGGREGATION",
                                                                                                   ENTRY_CATEGORY      : "Product",
                                                                                                   ENTRY_CATEGORY_FIELD: "productId"],
                                                                            CUSTOMER            : [LABEL        : "Customer(s)",
                                                                                                   UNIQUE_KEY   : "marginBreakdownCUSTOMER",
                                                                                                   DEFAULT_VALUE: null],
                                                                            CUSTOMER_AGGREGATION: [LABEL               : "Customer Aggregation",
                                                                                                   UNIQUE_KEY          : "marginBreakdownCUSTOMER_AGGREGATION",
                                                                                                   ENTRY_CATEGORY      : "Customer",
                                                                                                   ENTRY_CATEGORY_FIELD: "customerId"],
                                                                            YEAR                : [LABEL        : "Year",
                                                                                                   UNIQUE_KEY   : "marginBreakdownYEAR",
                                                                                                   FIELD_SUFFIX : YEAR_FIELD_SUFFIX,
                                                                                                   FORMAT       : YEAR_FORMAT,
                                                                                                   DEFAULT_VALUE: {
                                                                                                       return libs.SharedLib.DateUtils.currentYear()
                                                                                                   }],
                                                                            COMPARISON_YEAR     : [LABEL        : "Comparison Year",
                                                                                                   UNIQUE_KEY   : "marginBreakdownCOMPARISON_YEAR",
                                                                                                   DATE         : [FORMAT: "yyyy-MM-dd"],
                                                                                                   FIELD_SUFFIX : YEAR_FIELD_SUFFIX,
                                                                                                   FORMAT       : YEAR_FORMAT,
                                                                                                   DEFAULT_VALUE: {
                                                                                                       return libs.SharedLib.DateUtils.currentYear() - 1
                                                                                                   }],
                                                                            PERIOD              : [LABEL        : "Quarter",
                                                                                                   UNIQUE_KEY   : "marginBreakdownPERIOD",
                                                                                                   VALUES       : ["Q1", "Q2", "Q3", "Q4", EMPTY_DIMENSION_VALUE],
                                                                                                   DEFAULT_VALUE: {
                                                                                                       return libs.SIP_Dashboards_Commons.InputUtils.getCurrentQuarter()
                                                                                                   }],
                                                                            COMPARISON_PERIOD   : [LABEL        : "Comparison Quarter",
                                                                                                   UNIQUE_KEY   : "marginBreakdownCOMPARISON_PERIOD",
                                                                                                   VALUES       : ["Q1", "Q2", "Q3", "Q4", EMPTY_DIMENSION_VALUE],
                                                                                                   DEFAULT_VALUE: {
                                                                                                       return libs.SIP_Dashboards_Commons.InputUtils.getCurrentQuarter()
                                                                                                   }],
                                                                            SHOW_AS_PERCENTAGE  : [LABEL        : "Show Percentage (%)",
                                                                                                   UNIQUE_KEY   : "marginBreakdownSHOW_AS_PERCENTAGE",
                                                                                                   DEFAULT_VALUE: false],
                                                                            MODELS              : [LABEL        : "Calculation Type",
                                                                                                   UNIQUE_KEY   : "marginBreakdownMODELS",
                                                                                                   DEFAULT_VALUE: MARGIN_BREAKDOWN_NET_CHART_KEY],
                                                                            CURRENCY            : [LABEL     : "Currency",
                                                                                                   UNIQUE_KEY: "revenueBreakdownCURRENCY"]]]

/**
 * Holds all configuration of the Waterfall dashboard.
 * The configuration contains
 * - INPUTS - contains all information about available user inputs used in the dashboard. Used to generate default filters.
 */
@Field Map CAUSALITY_DASHBOARD_CONFIG = [INPUTS: [PRODUCT             : [LABEL        : "Product(s)",
                                                                         UNIQUE_KEY   : "causalityPRODUCT",
                                                                         DEFAULT_VALUE: null],
                                                  PRODUCT_AGGREGATION : [LABEL               : "Product Aggregation",
                                                                         UNIQUE_KEY          : "causalityPRODUCT_AGGREGATION",
                                                                         ENTRY_CATEGORY      : "Product",
                                                                         ENTRY_CATEGORY_FIELD: "productId"],
                                                  CUSTOMER            : [LABEL        : "Customer(s)",
                                                                         UNIQUE_KEY   : "causalityCUSTOMER",
                                                                         DEFAULT_VALUE: null],
                                                  CUSTOMER_AGGREGATION: [LABEL               : "Customer Aggregation",
                                                                         UNIQUE_KEY          : "causalityCUSTOMER_AGGREGATION",
                                                                         ENTRY_CATEGORY      : "Customer",
                                                                         ENTRY_CATEGORY_FIELD: "customerId"],
                                                  YEAR                : [LABEL        : "Year",
                                                                         UNIQUE_KEY   : "causalityYEAR",
                                                                         FIELD_SUFFIX : YEAR_FIELD_SUFFIX,
                                                                         FORMAT       : YEAR_FORMAT,
                                                                         DEFAULT_VALUE: {
                                                                             return libs.SharedLib.DateUtils.currentYear()
                                                                         }],
                                                  COMPARISON_YEAR     : [LABEL        : "Comparison Year",
                                                                         UNIQUE_KEY   : "causalityCOMPARISON_YEAR",
                                                                         DATE         : [FORMAT          : "yyyy-MM-dd",
                                                                                         MONTHS_BACK_FROM: -3,
                                                                                         MONTHS_BACK_TO  : -12],
                                                                         FIELD_SUFFIX : YEAR_FIELD_SUFFIX,
                                                                         FORMAT       : YEAR_FORMAT,
                                                                         DEFAULT_VALUE: {
                                                                             return libs.SharedLib.DateUtils.currentYear() - 1
                                                                         }],
                                                  PERIOD              : [LABEL        : "Quarter",
                                                                         UNIQUE_KEY   : "causalityPERIOD",
                                                                         VALUES       : ["Q1", "Q2", "Q3", "Q4", EMPTY_DIMENSION_VALUE],
                                                                         DEFAULT_VALUE: {
                                                                             return libs.SIP_Dashboards_Commons.InputUtils.getCurrentQuarter()
                                                                         }],
                                                  COMPARISON_PERIOD   : [LABEL        : "Comparison Quarter",
                                                                         UNIQUE_KEY   : "causalityCOMPARISON_PERIOD",
                                                                         VALUES       : ["Q1", "Q2", "Q3", "Q4", EMPTY_DIMENSION_VALUE],
                                                                         DEFAULT_VALUE: {
                                                                             return libs.SIP_Dashboards_Commons.InputUtils.getCurrentQuarter()
                                                                         }],
                                                  SHOW_AS_PERCENTAGE  : [LABEL        : "Show Percentage (%)",
                                                                         UNIQUE_KEY   : "causalitySHOW_AS_PERCENTAGE",
                                                                         DEFAULT_VALUE: false],
                                                  NO_OF_RESULTS       : [LABEL            : "Top Product(s)/ Customer(s)",
                                                                         NO_CUSTOMER_LABEL: "Top Product(s)",
                                                                         UNIQUE_KEY       : "causalityNO_OF_RESULTS",
                                                                         VALUES           : ["5", "10", "25", "50"],
                                                                         DEFAULT_VALUE    : "10"],
                                                  CURRENCY            : [LABEL     : "Currency",
                                                                         UNIQUE_KEY: "revenueBreakdownCURRENCY"]]]

/**
 * Defines the key used to describe the General Filter configurator in the DefaultFilters
 */
@Field String GENERAL_FILTER_NAME = "General Filter"

/**
 * Defines the key used to describe the Outliers configurator in the DefaultFilters
 */
@Field String OUTLIERS_NAME = "Outliers Dashboard"

/**
 * Defines the key used to describe the Revenue and Margin configurator in the DefaultFilters
 */
@Field String REVENUE_AND_MARGIN_NAME = "Revenue and Margin"

/**
 * Defines the key used to describe the Regional Revenue and Margin configurator in the DefaultFilters
 */
@Field String REVENUE_AND_MARGIN_MAP_NAME = "Regional Revenue and Margin"

/**
 * Defines the key used to describe the Waterfall configurator in the DefaultFilters
 */
@Field String WATERFALL_NAME = "Waterfall"

/**
 * Defines the key used to describe the Comparison Waterfall configurator in the DefaultFilters
 */
@Field String COMPARISON_WATERFALL_NAME = "Comparison Waterfall"

/**
 * Defines the key used to describe the Margin Breakdown configurator in the DefaultFilters
 */
@Field String MARGIN_BREAKDOWN_NAME = "Margin Breakdown"

/**
 * Defines the key used to describe the Revenue Breakdown configurator in the DefaultFilters
 */
@Field String REVENUE_BREAKDOWN_NAME = "Revenue Breakdown"

/**
 * Defines the key used to describe the Causality Dashboard configurator in the DefaultFilters
 */
@Field String PRODUCT_CUSTOMER_CAUSALITY_NAME = "Causality Dashboard"

/**
 * Defines the key used to describe the Period-over-Period Dashboard configurator in the DefaultFilters
 */
@Field String PERIOD_OVER_PERIOD_NAME = "Period-over-Period"

/**
 * Defines the order in which the configurators for the Default Filters will be displayed.
 * The GENERAL_FILTER is always displayed first.
 */
@Field List DEFINED_DASHBOARD_FILTERS = [REVENUE_AND_MARGIN_NAME,
                                         REVENUE_AND_MARGIN_MAP_NAME,
                                         OUTLIERS_NAME,
                                         WATERFALL_NAME,
                                         COMPARISON_WATERFALL_NAME,
                                         REVENUE_BREAKDOWN_NAME,
                                         MARGIN_BREAKDOWN_NAME,
                                         PRODUCT_CUSTOMER_CAUSALITY_NAME,
                                         PERIOD_OVER_PERIOD_NAME]

@Field Map DEFAULT_CONFIGURATOR_CONFIG = [INPUTS: [DASHBOARD_SELECTION: [LABEL     : "Dashboard",
                                                                         UNIQUE_KEY: "defaultConfiguratorDASHBOARD_SELECTION"],
                                                   GENERAL_FILTER     : [LABEL     : "General Filter",
                                                                         UNIQUE_KEY: "defaultConfiguratorGENERAL_FILTER"]]]

/**
 * The modes for Revenue breakdown and Margin breakdown dashboards.
 */
@Field Map BREAKDOWN_CALCULATION_DEFINITION = [LEGACY  : "Legacy",
                                           STANDARD: "Standard"]

/**
 * Defines the configuration for Mixpanel tracking.
 */
@Field Map MIX_PANEL_INFO = [ACCELERATOR_NAME  : "Sales Insights",
                             MODULE_NAME       : "Dashboards",
                             FIELD_NAME        : [DASHBOARD_LABEL: "Dashboard Label",
                                                  DASHBOARD_NAME : "Dashboard Name"],
                             DASHBOARD_PROPERTY: [CAUSALITY                  : [LOGIC_NAME     : "Dashboard_Causality",
                                                                                DASHBOARD_LABEL: "8. SI Causality Dashboard",
                                                                                DASHBOARD_NAME : "Causality_Dashboard"],
                                                  COMPARISON_WATERFALL       : [LOGIC_NAME     : "Dashboard_ComparisonWaterfall",
                                                                                DASHBOARD_LABEL: "5. SI Comparison Waterfall",
                                                                                DASHBOARD_NAME : "ComparisonWaterfall"],
                                                  MARGIN_BREAKDOWN           : [LOGIC_NAME     : "Dashboard_Margin_Breakdown",
                                                                                DASHBOARD_LABEL: "7. SI Margin Breakdown",
                                                                                DASHBOARD_NAME : "Margin_Breakdown"],
                                                  REVENUE_MARGIN             : [LOGIC_NAME     : "Dashboard_RevenueAndMargin",
                                                                                DASHBOARD_LABEL: "1. SI Revenue and Margin",
                                                                                DASHBOARD_NAME : "Revenue_Margin"],
                                                  REVENUE_MARGIN_DISTRIBUTION: [LOGIC_NAME     : "Dashboard_RevenueAndMarginDistribution_Country",
                                                                                DASHBOARD_LABEL: "2. SI Regional Revenue and Margin",
                                                                                DASHBOARD_NAME : "RevenueAndMarginDistribution_DetailMap"],
                                                  WATERFALL                  : [LOGIC_NAME     : "Dashboard_Waterfall",
                                                                                DASHBOARD_LABEL: "4. SI Waterfall",
                                                                                DASHBOARD_NAME : "Waterfall"],
                                                  OUTLIERS                   : [LOGIC_NAME     : "Outliers_Dashboard",
                                                                                DASHBOARD_LABEL: "3. SI Outliers Dashboard",
                                                                                DASHBOARD_NAME : "Outliers_Dashboard"],
                                                  REVENUE_BREAKDOWN          : [LOGIC_NAME     : "Revenue_Breakdown",
                                                                                DASHBOARD_LABEL: "6. SI Revenue Breakdown",
                                                                                DASHBOARD_NAME : "Revenue_Breakdown"],
                                                  PERIOD_OVER_PERIOD         : [LOGIC_NAME     : "Dashboard_PeriodOverPeriod",
                                                                                DASHBOARD_LABEL: "9. SI Period-over-Period Dashboard",
                                                                                DASHBOARD_NAME : "Dashboard_PeriodOverPeriod"]]]

/**
 * Retrieves additional configuration for the Revenue And Margin axis type.
 * The configuration has to be created like this because it's not possible to add fields from other libraries to @Field definitions directly.
 * @return additional configuration for axis type based on the libs.HighchartsLibrary.ConstConfig.AXIS_TYPES
 */
Map getRevenueAndMarginAxisTypeConfig() {
    Map axisTypes = libs.HighchartsLibrary.ConstConfig.AXIS_TYPES

    return [VALUES       : [(axisTypes.LINEAR)     : "Linear",
                            (axisTypes.LOGARITHMIC): "Logarithmic"],
            DEFAULT_VALUE: axisTypes.LINEAR]
}

/**
 * Retrieves the list of available configurators to be used by the DefaultFilters configurator.
 * The list is build using elements from libs.SIP_Dashboards_Commons.ConstConfig
 * @return list of available configurators to be used by the DefaultFilters configurator
 */
List getDefinedFilters() {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig

    return [commonsConstConfig.GENERAL_FILTER_NAME] + commonsConstConfig.DEFINED_DASHBOARD_FILTERS
}

/**
 * Column label "Category" used in SIP dashboards data tabs.
 */
@Field String DASHBOARD_DATA_TAB_CATEGORY_COLUMN_LABEL = "Category"

/**
 * Column label "Series 1" used in SIP dashboards data tabs.
 */
@Field String DASHBOARD_DATA_TAB_SERIES_1_COLUMN_LABEL = "Series 1"

/**
 * The monetary field type
 */
@Field final String DM_MONEY_FIELD_TYPE = "MONEY"

/**
 * Get DataTab fields format used in Causality dashboards.
 * @param isPercentageDisplayModel defines percentage display model or not
 * @return List of fields format
 */
List getDataTabFieldsFormat(boolean isPercentageView) {
    return [[LABEL        : DASHBOARD_DATA_TAB_CATEGORY_COLUMN_LABEL,
             RESULT_MATRIX: null],
            [LABEL        : DASHBOARD_DATA_TAB_SERIES_1_COLUMN_LABEL,
             RESULT_MATRIX: (isPercentageView ? FieldFormatType.PERCENT : FieldFormatType.NUMERIC)]]
}
