import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ConfiguratorEntry

/**
 * Returns the List of hierarchy levels that are defined to be used in the SIP_MapHierarchyConfig PP.
 * Example entry:
 * ["continent" : ["NAME"     : "Continent",
 *                 "SQL_FIELD": "continent",
 *                 "CONTAINS" : "country",
 *                 "LABEL"    : "Continent"],
 *                 ....
 * ]
 * @return hierarchy levels that are set to be used in Regional Revenue and Margin Dashboard
 */
Map getUsedHierarchyFields() {
    Map ppConfiguration = libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_PP
    Filter isUsedFilter = Filter.equal(ppConfiguration.COLUMN_CONFIG.IS_USED, ppConfiguration.IS_USED)
    List configurationData = getPPTableData(ppConfiguration, isUsedFilter)

    List usedHierarchyNames = configurationData.getAt(ppConfiguration.COLUMN_CONFIG.HIERARCHY)
    Map usedConstConfig = findUsedConstConfigEntries(usedHierarchyNames)

    return appendUserLabels(usedConstConfig, configurationData)
}

/**
 * Retrieves the PP data based on the provided PP configuration.
 * The PP configuration is stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_PP
 * Extracts the fields required for the fetch from the COLUMN_CONFIG of the ppConfiguration.
 * Appends additional filters if required.
 * @param ppConfiguration PP table configuration stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_PP
 * @param additionalFilters additional filters to be used for the data fetch
 * @return raw PP data
 */
List getPPTableData(Map ppConfiguration, Filter... additionalFilters) {
    return getPPTableData(ppConfiguration, ppConfiguration.COLUMN_CONFIG.values() as List, additionalFilters)
}

/**
 * Retrieves the PP data based on the provided PP configuration.
 * The PP configuration is stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_PP
 * Appends additional filters if required.
 * @param ppConfiguration PP table configuration stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_PP
 * @param fields fields to fetch from the PP, the field values are in PP format (key1, attribute1...)
 * @param additionalFilters additional filters to be used for the data fetch
 * @return raw PP data
 */
List getPPTableData(Map ppConfiguration, List fields, Filter... additionalFilters) {
    Map table = api.findLookupTable(ppConfiguration.NAME)

    return api.find(ppConfiguration.TYPE,
            0,
            api.getMaxFindResultsLimit(),
            null,
            fields,
            Filter.equal("lookupTable.id", table.id),
            *additionalFilters)
}

/**
 * Retrieves the map code overrides for a given hierarchy key.
 * @param hierarchyKey currently processed hierarchy key. Can be one of: world, continent, country, region
 * @param mapCodeOverrides grouped raw PP data of the map code overrides retrieved from the MapCodeOverrides element
 * @return List of map code overrides for a given hierarchy key.
 * The structure returned looks like this:
 * [["key1": "Continent",
 *   "key2": "AF",
 *   "attribute1": "Europe",
 *   "attribute2": null],
 *  ["key1": "Continent",
 *   "key2": "AS",
 *   "attribute1": "Asia",
 *   "attribute2": null],
 *  ...]
 */
List getMapCodeOverridesForHierarchy(String hierarchyKey, Map mapCodeOverrides) {
    Map hierarchyConstConfig = libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
    String mapCodeKey = hierarchyConstConfig.getAt(hierarchyKey).NAME

    return mapCodeOverrides.getAt(mapCodeKey)
}

/**
 * Returns processed query data for the user inputs used in Regional Revenue and Margin Configurator.
 * If required user data is translated into ISO Codes according to the defined overrides.
 * Additional information like highchart map code url and user labels are appended as well.
 * Example:
 * "continent" : ["AS" : ["LABEL"      : "Asia",
 *                        "ISO_CODE"   : "AS",
 *                        "MAP_CODE"   : "custom/asia",
 *                        "USER_LABEL" : null],
 *                "EU":  ["LABEL"     : "Europe",
 *                       "ISO_CODE"   : "EU",
 *                       "MAP_CODE"   : "custom/europe",
 *                       "USER_LABEL" : "User Europe"]]
 * @param queryData raw query data
 * @param mapCodeOverrides grouped raw PP data of the map code overrides retrieved from the MapCodeOverrides element
 * @return raw query data grouped by hierarchy levels with defined structure
 */
Map processConfiguratorQueryData(List queryData, Map mapCodeOverrides) {
    Map hierarchyConfig = libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
    Map continentEntries = getContinentEntries(hierarchyConfig, queryData, mapCodeOverrides)
    Map countryEntries = getCountryEntries(hierarchyConfig, queryData, mapCodeOverrides, continentEntries)
    Map regionEntries = getRegionEntries(hierarchyConfig, queryData, mapCodeOverrides, countryEntries)

    return [continent: continentEntries,
            country  : countryEntries,
            region   : regionEntries]
}

/**
 * Queries the data mart to retrieve all the data that is required by the configurator.
 * The fields retrieved from the data mart are dependent on the defined used fields.
 * @param usedFields a structure defining which hierarchy levels are in use as well as additional information about them.
 * The structure of an entry is:
 * ["continent": ["NAME": "Continent",
 *                "SQL_FIELD": "continent",
 *                "CONTAINS": "country",
 *                "LABEL": "Continent"]
 *  ...],
 * @return raw query data to be used by Regional Revenue and Margin Configurator
 */
List getConfiguratorQueryData(Map usedFields) {
    Map sqlConfiguration = libs.SIP_Dashboards_Commons.ConfigurationUtils.getAdvancedConfiguration([:], libs.SIP_Dashboards_Commons.ConstConfig.DASHBOARDS_ADVANCED_CONFIGURATION_NAME)

    Map<String, String> sqlFields = getSQLFields(usedFields, sqlConfiguration)

    Map queryDefinition = [datamartName: sqlConfiguration.datamartName,
                           fields      : sqlFields,
                           rollup      : true]

    return libs.HighchartsLibrary.QueryModule.queryDatamart(queryDefinition)
}

/**
 * Retrieves the map code overrides that are defined by the user.
 * The map code overrides allow to map the user data to ISO codes in case this is not the case.
 * Map code overrides also allow to override the default labels provided by the system.
 * The map codes are grouped by the hierarchy level they belong to.
 * The map codes are returned in raw PP key/attribute data.
 * The PP structure looks like this:
 * - key1 - defines the hierarchy level of a given entry
 * - key2 - defines the ISO code that will be mapped to the data provided
 * - attribute1 - defines the name for a ISO code mapped in key2 in the user data (allows to store data not in ISO codes)
 * - attribute2 - defines the customer user label for the entry
 * The structure of the map code overrides looks like follows:
 * ["Continent": [["key1": "Continent",
 *                 "key2": "AF",
 *                 "attribute1": "Europe",
 *                 "attribute2": null],
 *                ["key1": "Continent",
 *                "key2": "AS",
 *                "attribute1": "Asia",
 *                "attribute2": null],
 *                ...]
 *  "Country" :  [["key1": "Country",
 *                 "key2": "AU",
 *                 "attribute1": "Australia",
 *                 "attribute2": "Australia Label"],
 *                ["key1": "Country",
 *                "key2": "CN",
 *                "attribute1": "China",
 *                "attribute2": null],
 *                ...]],
 */
Map getMapCodeOverrides() {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig

    Map ppConfiguration = commonsConstConfig.REGIONAL_REVENUE_AND_MARGIN_MAP_CODES_PP
    Map columnConfig = commonsConstConfig.REGIONAL_REVENUE_AND_MARGIN_MAP_CODES_PP.COLUMN_CONFIG

    List overrides = libs.SIP_Dashboards_Commons.RegionalRevenueAndMarginUtils.getPPTableData(ppConfiguration)

    return overrides.groupBy { it.getAt(columnConfig.HIERARCHY_LEVEL) }
}

/**
 * Adds the configurator parameter representing the "Display world map" check box on the configurator.
 * If the world hierarchy is not used the creation is omitted.
 * @param configuratorEntry configurator to append the world parameter to
 * @param usedFields a structure defining which hierarchy levels are in use as well as additional information about them.
 * The structure of an entry is:
 * ["continent": ["NAME": "Continent",
 *                "SQL_FIELD": "continent",
 *                "CONTAINS": "country",
 *                "LABEL": "Continent"]
 *  ...],
 * @return boolean parameter to define whether the world map is to be displayed
 */
def addWorldConfiguratorParameter(ConfiguratorEntry configuratorEntry, Map usedFields) {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig
    String worldKey = commonsConstConfig.WORLD_HIERARCHY_KEY
    Map inputConfig = commonsConstConfig.REGIONAL_REVENUE_AND_MARGIN_CONFIGURATOR_CONFIG.INPUTS

    if (!usedFields.getAt(worldKey)) {
        return
    }

    Map worldInputConfig = inputConfig.getAt(worldKey)
    String worldEntryLabel = getConfiguratorEntryLabel(usedFields.getAt(worldKey))

    def parameter = configuratorEntry.createParameter(InputType.BOOLEANUSERENTRY, worldKey)
            .setLabel(sprintf(worldInputConfig.PATTERN, worldEntryLabel))
    libs.SIP_Dashboards_Commons.ConfiguratorUtils.setParameterDefaultValue(parameter, worldInputConfig.DEFAULT_VALUE)

    return parameter
}

/**
 * Adds a continent selection parameter to the provided configurator entry.
 * The visibility of the entry is dependent on the current value of the world map checkbox.
 * @param configuratorEntry configurator to append the continent parameter to
 * @param usedFields a structure defining which hierarchy levels are in use as well as additional information about them.
 * The structure of an entry is:
 * ["continent": ["NAME": "Continent",
 *                "SQL_FIELD": "continent",
 *                "CONTAINS": "country",
 *                "LABEL": "Continent"]
 *  ...],
 * @param worldEntryValue value of the world map checkbox. True if the map is displayed, false otherwise.
 * @param availableContinentsDefinition List of available continents to be displayed for the user.
 *                                      The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @param userDefaultValue value defined by the user for the given entry in the Default Filters configurator wizard.
 * @param isInit special flag used in the DefaultFilter wizard that defines whether the configurator is initialized
 * @return options parameter to allow selection of a continent
 */
def addContinentConfiguratorParameter(ConfiguratorEntry configuratorEntry,
                                      Map usedFields,
                                      boolean worldEntryValue,
                                      Map availableContinentsDefinition) {
    String continentKey = libs.SIP_Dashboards_Commons.ConstConfig.CONTINENT_HIERARCHY_KEY
    boolean isContinentEntryVisible = shouldDisplayEntry(usedFields, continentKey, worldEntryValue)

    if (!isContinentEntryVisible) {
        return
    }
    String continentEntryLabel = getConfiguratorEntryLabel(usedFields.getAt(continentKey))

    return addHierarchyConfiguratorParameter(configuratorEntry, continentEntryLabel, continentKey, availableContinentsDefinition)
}

/**
 * Adds a country selection parameter to the provided configurator entry.
 * The visibility of the entry is dependent on the current value of the world map checkbox and the value of the continent selection.
 * @param configuratorEntry configurator to append the country parameter to
 * @param usedFields a structure defining which hierarchy levels are in use as well as additional information about them.
 * The structure of an entry is:
 * ["continent": ["NAME": "Continent",
 *                "SQL_FIELD": "continent",
 *                "CONTAINS": "country",
 *                "LABEL": "Continent"]
 *  ...],
 * @param worldEntryValue value of the world map checkbox. True if the map is displayed, false otherwise.
 * @param selectedParentValue value of the continent selection, based on the provided value a mapping which values can be displayed is done
 * @param availableHierarchyDefinition List of available countries to be displayed for the user.
 *                                     The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @param userDefaultValue value defined by the user for the given entry in the Default Filters configurator wizard.
 * @param isInit special flag used in the DefaultFilter wizard that defines whether the configurator is initialized
 * @return options parameter to allow selection of a country
 */
def addCountryConfiguratorParameter(ConfiguratorEntry configuratorEntry,
                                    Map usedFields,
                                    boolean worldEntryValue,
                                    String selectedParentValue,
                                    Map availableHierarchyDefinition) {
    String hierarchyKey = libs.SIP_Dashboards_Commons.ConstConfig.COUNTRY_HIERARCHY_KEY

    return addHierarchyConfiguratorParameterWithParent(configuratorEntry,
            hierarchyKey,
            usedFields,
            worldEntryValue,
            selectedParentValue,
            availableHierarchyDefinition)
}

/**
 * Adds a region selection parameter to the provided configurator entry.
 * The visibility of the entry is dependent on the current value of the world map checkbox and the value of the country selection.
 * @param configuratorEntry configurator to append the region parameter to
 * @param usedFields a structure defining which hierarchy levels are in use as well as additional information about them.
 * The structure of an entry is:
 * ["continent": ["NAME": "Continent",
 *                "SQL_FIELD": "continent",
 *                "CONTAINS": "country",
 *                "LABEL": "Continent"]
 *  ...],
 * @param worldEntryValue value of the world map checkbox. True if the map is displayed, false otherwise.
 * @param selectedParentValue value of the country selection, based on the provided value a mapping which values can be displayed is done
 * @param availableHierarchyDefinition List of available regions to be displayed for the user.
 *                                     The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @param userDefaultValue value defined by the user for the given entry in the Default Filters configurator wizard.
 * @param isInit special flag used in the DefaultFilter wizard that defines whether the configurator is initialized
 * @return options parameter to allow selection of a region
 */
def addRegionConfiguratorParameter(ConfiguratorEntry configuratorEntry,
                                   Map usedFields,
                                   boolean worldEntryValue,
                                   String selectedParentValue,
                                   Map availableHierarchyDefinition) {
    String hierarchyKey = libs.SIP_Dashboards_Commons.ConstConfig.REGION_HIERARCHY_KEY

    return addHierarchyConfiguratorParameterWithParent(configuratorEntry,
            hierarchyKey,
            usedFields,
            worldEntryValue,
            selectedParentValue,
            availableHierarchyDefinition)
}

/**
 * Adds a configurator parameter defined by the hierarchy key to the provided configurator entry.
 * @param configuratorEntry configurator to append the region parameter to
 * @param hierarchyKey hierarchy key defining which hierarchy is currently being processed
 * @param usedFields a structure defining which hierarchy levels are in use as well as additional information about them.
 * The structure of an entry is:
 * ["continent": ["NAME": "Continent",
 *                "SQL_FIELD": "continent",
 *                "CONTAINS": "country",
 *                "LABEL": "Continent"]
 *  ...],
 * @param worldEntryValue value of the world map checkbox. True if the map is displayed, false otherwise.
 * @param selectedParentValue value of the parent selection, based on the provided value a mapping which values can be displayed is done
 * @param availableHierarchyDefinition List of available maps to be displayed for the user for a given hierarchy.
 *                                     The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @param userDefaultValue value defined by the user for the given entry in the Default Filters configurator wizard.
 * @param isInit special flag used in the DefaultFilter wizard that defines whether the configurator is initialized
 * @return configurator parameter defining the selection of a given hierarchy key
 */
def addHierarchyConfiguratorParameterWithParent(ConfiguratorEntry configuratorEntry,
                                                String hierarchyKey,
                                                Map usedFields,
                                                boolean worldEntryValue,
                                                String selectedParentValue,
                                                Map availableHierarchyDefinition) {
    boolean isEntryVisible = shouldDisplayEntry(usedFields, hierarchyKey, worldEntryValue, selectedParentValue)

    if (!isEntryVisible) {
        return
    }

    String entrylabel = getConfiguratorEntryLabel(usedFields.getAt(hierarchyKey))
    Map filteredDefinition = filterBasedOnParentHierarchy(availableHierarchyDefinition, selectedParentValue)

    return addHierarchyConfiguratorParameter(configuratorEntry, entrylabel, hierarchyKey, filteredDefinition)
}

/**
 * Prepares the structure of the hierarchy data based on the provided query data, overrides and available definitions.
 * The returned structure looks like this:
 * "continent" : ["AS" : ["LABEL"      : "Asia",
 *                        "ISO_CODE"   : "AS",
 *                        "MAP_CODE"   : "custom/asia",
 *                        "USER_LABEL" : null],
 *                "EU" : ["LABEL"     : "Europe",
 *                        "ISO_CODE"   : "EU",
 *                        "MAP_CODE"   : "custom/europe",
 *                        "USER_LABEL" : "User Europe"]]
 * The appropriate overrides and user labels are appended.
 * @param queryData raw query data
 * @param mapCodeOverrides grouped raw PP data of the map code overrides retrieved from the MapCodeOverrides element
 * @param hierarchyConfig configuration for a given currently processed hierarchy.
 *                        Stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
 * @param definedHierarchiesDefinition List of available maps to be displayed for the user for a given hierarchy.
 *                                     The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @return processed data to be used by the configurator, contains information for one hierarchy
 */
Map getUserHierarchies(List queryData, Map mapCodeOverrides, Map hierarchyConfig, Map definedHierarchiesDefinition) {
    List hierarchyOverrides = getHierarchyLevelOverrides(queryData, mapCodeOverrides, hierarchyConfig)

    return getAvailableHierarchies(queryData, hierarchyOverrides, hierarchyConfig, definedHierarchiesDefinition)
}

/**
 * Prepares the structure of the hierarchy data based on the provided query data, overrides and available definitions.
 * The returned structure looks like this:
 * "country" : ["PL" : ["PARENT" : "EU",
 *                      "LABEL"      : "Poland",
 *                      "ISO_CODE"   : "PL",
 *                      "MAP_CODE"   : "countries/pl/pl-all",
 *                      "USER_LABEL" : null]]
 * The appropriate overrides and user labels are appended.
 * @param queryData raw query data
 * @param mapCodeOverrides grouped raw PP data of the map code overrides retrieved from the MapCodeOverrides element
 * @param hierarchyConfig configuration for a given currently processed hierarchy.
 *                        Stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
 * @param definedHierarchiesDefinition List of available maps to be displayed for the user for a given hierarchy.
 *                                     The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @param parentHierarchy parent hierarchy processed data as defined by getUserHierarchies
 * @return processed data to be used by the configurator, contains information for one hierarchy
 */
Map getUserHierarchies(List queryData,
                       Map codeOverrides,
                       Map hierarchyConfig,
                       Map definedHierarchiesDefinition,
                       Map parentHierarchy) {
    List hierarchyOverrides = getHierarchyLevelOverrides(queryData, codeOverrides, hierarchyConfig)
    Map availableHierarchies = getAvailableHierarchies(queryData, hierarchyOverrides, hierarchyConfig, definedHierarchiesDefinition)

    return findAvailableHierarchiesBasedOnParent(parentHierarchy, availableHierarchies)
}

/**
 * Retrieves the label for a given hierarchy entry.
 * If the label is defined it's used.
 * @param usedField a structure defining a single used field entry
 * The structure of an entry is:
 * ["continent": ["NAME": "Continent",
 *                "SQL_FIELD": "continent",
 *                "CONTAINS": "country",
 *                "LABEL": "Continent"]],
 * @return label to be displayed for a user for a given hierarchy input
 */
String getConfiguratorEntryLabel(Map usedField) {
    return usedField.LABEL ?: usedField.NAME
}

/**
 * Checks whether a given hierarchy level should be displayed for the user based on the usedFields.
 * Parent dependent inputs like country and region require the 4th parameter selectedParent to be filled for proper assessment.
 * @param usedFields a structure defining which hierarchy levels are in use as well as additional information about them.
 * The structure of an entry is:
 * ["continent": ["NAME": "Continent",
 *                "SQL_FIELD": "continent",
 *                "CONTAINS": "country",
 *                "LABEL": "Continent"]
 *  ...],
 * @param hierarchyKey hierarchy key defining which hierarchy is currently being processed
 * @param isWorldDisplayed value of the world map checkbox. True if the map is displayed, false otherwise.
 * @param selectedParentValue value of the continent selection, based on the provided value a mapping which values can be displayed is done
 * @return true if the entry should be displayed based on provided data, false otherwise.
 */
boolean shouldDisplayEntry(Map usedFields, String hierarchyKey, boolean isWorldDisplayed, String selectedParent = null) {
    return usedFields.getAt(hierarchyKey) && !isWorldDisplayed && (selectedParent ?: true)
}

/**
 * Retrieves the hierarchy level overrides based on the data availability and overrides
 * @param queryData raw query data
 * @param mapCodeOverrides grouped raw PP data of the map code overrides retrieved from the MapCodeOverrides element
 * @param hierarchyConfig configuration for a given currently processed hierarchy.
 *                        Stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
 * @return retrieves a list of hierarchy overrides for all hierarchies that have query data
 */
protected List getHierarchyLevelOverrides(List queryData, Map mapCodeOverrides, Map hierarchyConfig) {
    Map columnConfig = libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_MAP_CODES_PP.COLUMN_CONFIG
    String hierarchyName = hierarchyConfig.NAME
    List uniqueHierarchyData = queryData.getAt(hierarchyConfig.SQL_FIELD).unique()
    List hierarchyOverrides = mapCodeOverrides.getAt(hierarchyName)

    return hierarchyOverrides.findAll { Map overrideEntry ->
        uniqueHierarchyData.contains(overrideEntry.getAt(columnConfig.DM_FIELD_LABEL))
    }
}

/**
 * For a given query results returns a Map of a given hierarchy configuration as stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig,
 * with appended user label if such exists.
 * Allows to retrieve the supported list of hierarchy entries for further processing ex. retrieving the map code.
 * @param queryData raw query data
 * @param hierarchyOverrides a List of hierarchy map code overrides for one of the hierarchy levels
 * Example for continent:
 * ["key1": "Continent",
 *  "key2": "AS",
 *  "attribute1": "Asia"]
 * @param hierarchyConfig configuration for a given currently processed hierarchy.
 *                        Stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
 * @param definedHierarchies List of available maps to be displayed for the user for a given hierarchy.
 *                           The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @return a structure of available and defined hierarchy entries for a given hierarchy config.
 * "country" : ["PL" : ["PARENT" : "EU",
 *                      "LABEL"      : "Poland",
 *                      "ISO_CODE"   : "PL",
 *                      "MAP_CODE"   : "countries/pl/pl-all",
 *                      "USER_LABEL" : null]]
 */
protected Map getAvailableHierarchies(List queryData, List hierarchyOverrides, Map hierarchyConfig, Map definedHierarchies) {
    List uniqueHierarchyData = queryData.getAt(hierarchyConfig.SQL_FIELD).unique()

    return uniqueHierarchyData.collectEntries { String hierarchy ->
        return getHierarchyWithUserLabel(hierarchy, hierarchyOverrides, definedHierarchies)
    }.sort { it.value.USER_LABEL ?: it.value.LABEL }
}

/**
 * Retrieves a hierarchy user hierarchy label if such exists and appends it to the found hierarchy definition.
 * If no hierarchy will be found that means its not available for use therefore an empty map is returned
 * @param queryHierarchyName name of the hierarchy as returned by the query element.
 * @param hierarchyOverrides a List of hierarchy map code overrides for one of the hierarchy levels
 * Example for continent:
 * ["key1": "Continent",
 *  "key2": "AS",
 *  "attribute1": "Asia"]
 * @param definedHierarchies List of available maps to be displayed for the user for a given hierarchy.
 *                           The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @return hierarchy configuration as stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig for a given query result
 *         with additional user label appended if found
 */
protected Map getHierarchyWithUserLabel(String queryHierarchyName, List hierarchyOverrides, Map definedHierarchies) {
    Map columnConfig = libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_MAP_CODES_PP.COLUMN_CONFIG

    Map hierarchyOverride = findHierarchyOverrideByDMFieldLabel(hierarchyOverrides, queryHierarchyName)
    String hierarchyISOCode = getHierarchyISOCode(hierarchyOverride, queryHierarchyName)

    Map.Entry foundHierarchy = findDefinedHierarchyByISO(definedHierarchies, hierarchyISOCode)

    if (!foundHierarchy) {
        return [:]
    }

    foundHierarchy.value << [USER_LABEL: hierarchyOverride.getAt(columnConfig.DISPLAY_LABEL)]

    return [(foundHierarchy.key): foundHierarchy.value]
}

/**
 * Looks through the defined hierarchies structure and returns the one with matching iso code
 * @param definedHierarchies List of available maps to be displayed for the user for a given hierarchy.
 *                           The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @param hierarchyISOCode the ISO code to retrieve from the defined hierarchies
 * @return a Map.Entry of the defined hierarchy with given ISO code
 */
protected Map.Entry findDefinedHierarchyByISO(Map definedHierarchies, String hierarchyISOCode) {
    return definedHierarchies.find { String hierarchyKey, Map hierarchySettings ->
        hierarchyKey == hierarchyISOCode
    }
}

/**
 * Looks through the hierarchy overrides structure and returns the one with matching DM label
 * @param hierarchyOverrides a List of hierarchy map code overrides for one of the hierarchy levels
 * Example for continent:
 * ["key1": "Continent",
 *  "key2": "AS",
 *  "attribute1": "Asia"]
 * @param queryHierarchyName name of the hierarchy as returned by the query element.
 * @return Map from hierarchyOverrides whose DM_FIELD_LABEL matches the provided name returned from the query
 */
protected Map findHierarchyOverrideByDMFieldLabel(List hierarchyOverrides, String queryHierarchyName) {
    Map columnConfig = libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_MAP_CODES_PP.COLUMN_CONFIG

    return hierarchyOverrides.find { it.getAt(columnConfig.DM_FIELD_LABEL) == queryHierarchyName }
}

/**
 * Retrieves the ISO code for a given hierarchy override, if none is found a fallback value is used.
 * @param hierarchyOverride hierarchy map code override for one of the hierarchy entries
 * Example:
 * ["key1": "Continent",
 *  "key2": "AS",
 *  "attribute1": "Asia"]
 * @param fallbackValue fallback value that will be used in case no ISO code will be found
 * @return ISO code of a given hierarchy override or fallback value
 */
protected String getHierarchyISOCode(Map hierarchyOverride, String fallbackValue) {
    def commonsConstConfig = libs.SIP_Dashboards_Commons.ConstConfig
    Map columnConfig = commonsConstConfig.REGIONAL_REVENUE_AND_MARGIN_MAP_CODES_PP.COLUMN_CONFIG

    return hierarchyOverride?.getAt(columnConfig.ISO_CODE) ?: fallbackValue
}

/**
 * Retrieves all available hierarchies for a given parent.
 * Used to retrieve all defined countries for a given continent for example.
 * @param parentHierarchy parent hierarchy processed data as defined by getUserHierarchies
 * @param availableHierarchies List of available maps to be displayed for the user for a given hierarchy.
 *                             The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @return all available hierarchies for a given parent
 */
protected Map findAvailableHierarchiesBasedOnParent(Map parentHierarchy, Map availableHierarchies) {
    return availableHierarchies.findAll { Map.Entry it -> return parentHierarchy.containsKey(it.value.PARENT.ISO_CODE) }
}

/**
 * Retrieves all available hierarchies for a given parent ISO code.
 * Used to retrieve all defined countries for a given continent for example.
 * @param hierarchyLevel List of available maps to be displayed for the user for a given hierarchy.
 *                              The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @param parentHierarchy ISO code of the parent hierarchy
 * @return available hierarchy levels based on the parent ISO code
 */
protected Map filterBasedOnParentHierarchy(Map hierarchyLevel, String parentHierarchy) {
    if (!parentHierarchy) {
        return null
    }

    return hierarchyLevel.findAll { Map.Entry it -> it.value.PARENT.ISO_CODE == parentHierarchy }
}

/**
 * Adds the hierarchy selection parameter to an existing configurator entry.
 * @param configuratorEntry the configurator entry to which the parameter will be appended
 * @param configuratorLabel display label for the configurator parameter
 * @param configuratorKey unique key for the configurator parameter
 * @param availableHierarchiesDefinition List of available maps to be displayed for the user for a given hierarchy.
 *                                       The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @param userDefaultValue value defined by the user for the given entry in the Default Filters configurator wizard.
 * @param isInit special flag used in the DefaultFilter wizard that defines whether the configurator is initialized
 * @return a configurator entry with appended hierarchy parameter
 */
protected def addHierarchyConfiguratorParameter(ConfiguratorEntry configuratorEntry,
                                                String configuratorLabel,
                                                String configuratorKey,
                                                Map availableHierarchiesDefinition) {
    if (!availableHierarchiesDefinition) {
        return
    }

    Map configuratorLabeledValues = getHierarchyLabels(availableHierarchiesDefinition)
    List configuratorValues = configuratorLabeledValues.keySet() as List
    def parameter = configuratorEntry.createParameter(InputType.OPTION, configuratorKey)
            .setLabel(configuratorLabel)
            .setValueOptions(configuratorValues)

    parameter.addParameterConfigEntry("labels", configuratorLabeledValues)

    libs.SIP_Dashboards_Commons.ConfiguratorUtils.setParameterDefaultValue(parameter, null)

    libs.SIP_Dashboards_Commons.ConfiguratorUtils.assertCurrentConfiguratorValue(parameter, configuratorValues)

    return parameter
}

/**
 * Retrieves the key-label mapping for the hierarchy selection options.
 * If the user has a defined a custom label it is used for the display instead.
 * @param availableHierarchiesDefinition List of available maps to be displayed for the user for a given hierarchy.
 *                                       The list is stored in libs.SIP_Dashboards_Commons.MapCodesConstConfig
 * @return mapping for options labels to be used by hierarchy selection parameters
 */
protected Map getHierarchyLabels(Map availableHierarchiesDefinition) {
    return availableHierarchiesDefinition.collectEntries { String hierarchyKey, Map hierarchySettings ->
        [(hierarchyKey): (hierarchySettings.USER_LABEL ?: hierarchySettings.LABEL)]
    }
}

/**
 * Retrieves the SQL_FIELDS to be fetched from the datamart based on the provided usedFields definition
 * @param usedFields a structure defining which hierarchy levels are in use as well as additional information about them.
 * The structure of an entry is:
 * ["continent": ["NAME": "Continent",
 *                "SQL_FIELD": "continent",
 *                "CONTAINS": "country",
 *                "LABEL": "Continent"]
 *  ...],
 * @param sqlConfiguration sql configuration containing data mart fields mapping definition. Stored in SIP_AdvancedConfiguration.
 * @return Map containing the alias and the field name for a given user fields definition
 */
protected Map<String, String> getSQLFields(Map usedFields, Map sqlConfiguration) {
    return usedFields.collectEntries { Map.Entry<String, Map> it ->
        return it.value?.SQL_FIELD ?
                [(it.value.SQL_FIELD): sqlConfiguration.getAt(it.value.SQL_FIELD)] :
                [:]
    }
}

/**
 * Retrieves the const configs for the used hierarchies.
 * The const configs for the hierarchies are stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
 * @param usedHierarchyNames List of used hierarchy names retrieved from SIP_MapHierarchyConfig PP table
 * @return const config configuration for a given hierarchy names
 */
protected Map findUsedConstConfigEntries(List usedHierarchyNames) {
    Map configuratorConfig = libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG

    return configuratorConfig.findAll { String hierarchyName, Map hierarchySettings -> usedHierarchyNames.contains(hierarchySettings.NAME) }
}

/**
 * Appends the user labels to the const config of the used fields to create a complete structure defining a single used field entry
 * @param usedConstConfig const config retrieved from findUsedConstConfigEntries
 * @param configurationData raw PP configuration data retrieved from getPPTableData for SIP_MapHierarchyConfig PP table
 * @return usedConstConfig entries with user label appended.
 */
protected Map appendUserLabels(Map usedConstConfig, List configurationData) {
    Map columnConfig = libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_PP.COLUMN_CONFIG

    return usedConstConfig.collectEntries { String entryName, Map entrySettings ->
        String userLabel = configurationData.find { entrySettings.NAME == it.getAt(columnConfig.HIERARCHY) }
                .getAt(columnConfig.LABEL)

        entrySettings << [LABEL: userLabel]

        return [(entryName): entrySettings]
    }
}

/**
 * Retrieves the structure for the continent hierarchy for the processed query data for Regional Revenue and Margin Configurator
 * @param hierarchyConfig configuration for a given currently processed hierarchy.
 *                        Stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
 * @param queryData raw query data
 * @param mapCodeOverrides grouped raw PP data of the map code overrides retrieved from the MapCodeOverrides element
 * @return processed data to be used by the configurator, contains information for continent hierarchy
 */
protected Map getContinentEntries(Map hierarchyConfig, List queryData, Map mapCodeOverrides) {
    Map continentConfig = hierarchyConfig.getAt(libs.SIP_Dashboards_Commons.ConstConfig.CONTINENT_HIERARCHY_KEY)
    Map definedContinents = libs.SIP_Dashboards_Commons.MapCodesConstConfig.CONTINENT_MAP_CODES

    return getUserHierarchies(queryData, mapCodeOverrides, continentConfig, definedContinents)
}

/**
 * Retrieves the structure for the country hierarchy for the processed query data for Regional Revenue and Margin Configurator
 * @param hierarchyConfig configuration for a given currently processed hierarchy.
 *                        Stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
 * @param queryData raw query data
 * @param mapCodeOverrides grouped raw PP data of the map code overrides retrieved from the MapCodeOverrides element
 * @param continentEntries continent hierarchy processed data as defined by getUserHierarchies
 * @return processed data to be used by the configurator, contains information for country hierarchy
 */
protected Map getCountryEntries(Map hierarchyConfig, List queryData, Map mapCodeOverrides, Map continentEntries) {
    Map countryConfig = hierarchyConfig.getAt(libs.SIP_Dashboards_Commons.ConstConfig.COUNTRY_HIERARCHY_KEY)
    Map definedCountries = libs.SIP_Dashboards_Commons.MapCodesConstConfig.COUNTRY_MAP_CODES

    return getUserHierarchies(queryData, mapCodeOverrides, countryConfig, definedCountries, continentEntries)
}

/**
 * Retrieves the structure for the region hierarchy for the processed query data for Regional Revenue and Margin Configurator
 * @param hierarchyConfig configuration for a given currently processed hierarchy.
 *                        Stored in libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_HIERARCHY_CONFIG
 * @param queryData raw query data
 * @param mapCodeOverrides grouped raw PP data of the map code overrides retrieved from the MapCodeOverrides element
 * @param countryEntries country hierarchy processed data as defined by getUserHierarchies
 * @return processed data to be used by the configurator, contains information for region hierarchy
 */
protected Map getRegionEntries(Map hierarchyConfig, List queryData, Map mapCodeOverrides, Map countryEntries) {
    Map regionConfig = hierarchyConfig.getAt(libs.SIP_Dashboards_Commons.ConstConfig.REGION_HIERARCHY_KEY)
    Map definedRegions = libs.SIP_Dashboards_Commons.MapCodesConstConfig.REGION_MAP_CODES

    return getUserHierarchies(queryData, mapCodeOverrides, regionConfig, definedRegions, countryEntries)
}

/**
 * Retrieves the configs for supported KPIs. The supported KPIs vary depending whether the customer data is used or not.
 * @param isCustomerDataUsed states whether the customer data is mapped and used by the package
 * @return Map containing configs for supported KPIs
 */
protected Map getSupportedKPI(boolean isCustomerDataUsed) {
    Map dashboardConfig = libs.SIP_Dashboards_Commons.ConstConfig.REGIONAL_REVENUE_AND_MARGIN_DASHBOARD_CONFIG
    Map kpiConfig = dashboardConfig.KPI
    Map customerSpecificKPIs = kpiConfig.findAll { String kpiEntryKey, Map kpiEntryConfig -> kpiEntryConfig.REQUIRES_CUSTOMER_DATA == true }

    if (!isCustomerDataUsed) {
        kpiConfig -= customerSpecificKPIs
    }

    return kpiConfig
}