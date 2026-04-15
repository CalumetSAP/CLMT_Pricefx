import groovy.transform.Field
import com.googlecode.genericdao.search.Filter

@Field String ERROR_MESSAGE_SEPARATOR = "::"
@Field Map ERROR_TYPES = [NO_CONFIG   : "NO_CONFIG",
                          EMPTY_CONFIG: "EMPTY_CONFIG"]

/**
 * Read specific entry of config and create output with configured custom values instead of attributeId
 * Calling getConfigEntry("PPName", columnConfig, "CostType1", "CostDefinition1") will return a PP entry which
 * has CostType1 set as key1 and CostDefinition1 as key2
 * @param parameterName - PP where config is stored
 * @param columnConfig - Info about meta. Must be in following format:
 *  codeName1: [LABEL: "customKey1", PARAM: "attribute1"],
 *  codeName2: [LABEL: "customKey2", PARAM: "attribute2"],
 *  codeName3: [LABEL: "customKey3", PARAM: "attribute3", OPTIONAL : true]]
 * @param keys - values of keys used in a given PP to retrieve the config for
 * @return PP entry with configured custom values instead of attributes id
 * @throws - no data can be found in specified Price Parameter. Message will start with ERROR_TYPES.NO_CONFIG.
 *         - data not marked as OPTIONAL in columnConfig is missing from Price Parameter. Message will start with ERROR_TYPES.EMPTY_CONFIG.
 */
Map getConfigEntry(String parameterName, Map columnConfig, String... keys) {
    Filter parameterFilter = buildFilterForConfigEntry(*keys)

    return readConfig(parameterName, columnConfig, parameterFilter)?.getAt(0)
}

/**
 * Read specific entry of config and create output with configured custom values instead of attributeId
 * @param parameterName - PP where config is stored
 * @param columnConfig - Info about meta. Must be in following format:
 *  codeName1: [LABEL: "customKey1", PARAM: "attribute1"],
 *  codeName2: [LABEL: "customKey2", PARAM: "attribute2"],
 *  codeName3: [LABEL: "customKey3", PARAM: "attribute3", OPTIONAL : true]]
 * @param extendedFilter - Additional filter to be applied for lookup, ignored if null
 * @param keys - values of keys used in a given PP to retrieve the config for
 * @return PP entry with configured custom values instead of attributes id
 * @throws - no data can be found in specified Price Parameter. Message will start with ERROR_TYPES.NO_CONFIG.
 *         - data not marked as OPTIONAL in columnConfig is missing from Price Parameter. Message will start with ERROR_TYPES.EMPTY_CONFIG.
 */
Map getConfigEntry(String parameterName, Map columnConfig, Filter extendedFilter, String... keys) {
    Filter parameterFilter = buildFilterForConfigEntry(*keys)
    Filter extensionFilter = extendedFilter ? Filter.and(parameterFilter, extendedFilter) : parameterFilter

    return readConfig(parameterName, columnConfig, extensionFilter)?.getAt(0)
}

/**
 * Read entries of config and create output with configured custom values instead of attributeId.
 * Applies additional extended filter to the lookups.
 * @param columnConfig - Info about meta. Must be in following format:
 *  codeName1: [LABEL: "customKey1", PARAM: "attribute1"],
 *  codeName2: [LABEL: "customKey2", PARAM: "attribute2"],
 *  codeName3: [LABEL: "customKey3", PARAM: "attribute3", OPTIONAL : true]]
 * @param sortBy - Sort by field
 * @param extendedFilter - Additional filter to be applied for lookup, ignored if null
 * @param keys - values of keys used in a given PP to retrieve the config for
 * @return PP entries with configured custom values instead of attributes id
 * @throws - no data can be found in specified Price Parameter. Message will start with ERROR_TYPES.NO_CONFIG.
 *         - data not marked as OPTIONAL in columnConfig is missing from Price Parameter. Message will start with ERROR_TYPES.EMPTY_CONFIG.
 */
List<Map> getConfigEntries(String parameterName, Map columnConfig, String sortBy, Filter extendedFilter, String... keys) {
    Filter parameterFilter = buildFilterForConfigEntry(*keys)
    Filter extensionFilter = extendedFilter ? Filter.and(parameterFilter, extendedFilter) : parameterFilter

    return readConfig(parameterName, columnConfig, sortBy, extensionFilter)
}

/**
 * Read entries of config and create output with configured custom values instead of attributeId.
 * Applies additional extended filter to the lookups.
 * @param columnConfig - Info about meta. Must be in following format:
 *  codeName1: [LABEL: "customKey1", PARAM: "attribute1"],
 *  codeName2: [LABEL: "customKey2", PARAM: "attribute2"],
 *  codeName3: [LABEL: "customKey3", PARAM: "attribute3"]]
 * @param extendedFilter - Additional filter to be applied for lookup, ignored if null
 * @param keys - keys used in a given PP
 * @return PP entries with configured custom values instead of attributes id
 */
List<Map> getConfigEntries(String parameterName, Map columnConfig, Filter extendedFilter, String... keys) {
    return getConfigEntries(parameterName, columnConfig, "id", extendedFilter, keys)
}

/**
 * Read entries of config and create output with configured custom values instead of attributeId.
 * Applies additional extended filter to the lookups.
 * @param columnConfig - Info about meta. Must be in following format:
 *  codeName1: [LABEL: "customKey1", PARAM: "attribute1"],
 *  codeName2: [LABEL: "customKey2", PARAM: "attribute2"],
 *  codeName3: [LABEL: "customKey3", PARAM: "attribute3"]]
 * @param keys - keys used in a given PP
 * @return PP entries with configured custom values instead of attributes id
 */
List<Map> getConfigEntries(String parameterName, Map columnConfig, String... keys) {
    Filter parameterFilter = buildFilterForConfigEntry(*keys)

    return readConfig(parameterName, columnConfig, parameterFilter)
}

/**
 * Read PP and create output with configured custom values instead of attributeId
 * @param parameterName - PP name
 * @param columnConfig - Must be in following format:
 * [codeName1 : [LABEL: "customKey1", PARAM: "attribute1"],
 *  codeName2: [LABEL: "customKey2", PARAM: "attribute2"],
 *  codeName3: [LABEL: "customKey3", PARAM: "attribute3", OPTIONAL : true]]
 * @param filters - Input filters. If null, will fetch whole PP. It accepts Filter object, not Array, so remember to use Filter.and(*filters) if needed
 * @return Configuration data transformed according to provided ColumnConfig
 * @throws - no data can be found in specified Price Parameter. Message will start with ERROR_TYPES.NO_CONFIG.
 *         - data not marked as OPTIONAL in columnConfig is missing from Price Parameter. Message will start with ERROR_TYPES.EMPTY_CONFIG.
 */
List<Map> readConfig(String parameterName, Map columnConfig, Filter... filters) {
    return readConfig(parameterName, columnConfig, "id", *filters)
}

/**
 * Read PP and create output with configured custom values instead of attributeId
 * @param parameterName - PP name
 * @param columnConfig - Must be in following format:
 * [codeName1 : [LABEL: "customKey1", PARAM: "attribute1"],
 *  codeName2: [LABEL: "customKey2", PARAM: "attribute2"],
 *  codeName3: [LABEL: "customKey3", PARAM: "attribute3", OPTIONAL : true]]
 * @param filters - Input filters. If null, will fetch whole PP. It accepts Filter object, not Array, so remember to use Filter.and(*filters) if needed
 * @return Configuration data transformed according to provided ColumnConfig
 * @throws - no data can be found in specified Price Parameter. Message will start with ERROR_TYPES.NO_CONFIG.
 *         - data not marked as OPTIONAL in columnConfig is missing from Price Parameter. Message will start with ERROR_TYPES.EMPTY_CONFIG.
 */
List<Map> readConfig(String parameterName, Map columnConfig, String sortBy, Filter... filters) {
    List ppData = api.findLookupTableValues(parameterName, sortBy, *filters)

    if (!ppData) {
        api.throwException("${ERROR_TYPES.NO_CONFIG}${ERROR_MESSAGE_SEPARATOR}Cannot load source configuration from $parameterName for $filters")
    }
    def result = transformIdKeysIntoMetadataKeys(ppData, columnConfig)
    validateData(columnConfig, result, parameterName)

    return result
}

protected Filter buildFilterForConfigEntry(String keyValue) {
    return Filter.equal("name", keyValue)
}

/**
 * Method used to build @Filter object based on passed keys values, Filters have form of "Filter.equal("key$index",keyValue)" where keyValue is
 * a String passed as param and index is its order of forwarding to the method.
 * @param keys - key values passed in ascending order
 * @return Filter.and( ) object with multiple keyFilters inside
 */
protected Filter buildFilterForConfigEntry(String... keys) {
    List parsedKeys = keys as List
    List keyFilters = parsedKeys.withIndex(1)
            .collect { keyValue, index ->
                return keyValue ? Filter.equal("key$index", keyValue) : null
            }
            .findAll { it != null }

    return Filter.and(*keyFilters)
}


/**
 * Validates pp row and throws exception when data are corrupted
 * @param columnConfig - column config from Configuration
 * @param result - row from PP mapped according to ColumnConfig
 * @param parameterName - name of lookuped PP
 * @throws - data not marked as OPTIONAL in columnConfig is missing from Price Parameter. Message will start with ERROR_TYPES.EMPTY_CONFIG.
 */
protected void validateData(Map columnConfig, List<Map> result, String parameterName) {
    if (isRequiredDataMissing(columnConfig, result)) {
        api.throwException("${ERROR_TYPES.EMPTY_CONFIG}${ERROR_MESSAGE_SEPARATOR}Check data in $parameterName, read config is: $result")
    }

    return
}

/**
 * Method that verifies whether all required information, according to given column config, is present
 * @param columnConfig - column config from configuration, defining which fields are required and which optional.
 * Must be in following format:
 *  codeName1: [LABEL: "customKey1", PARAM: "attribute1"],
 *  codeName2: [LABEL: "customKey2", PARAM: "attribute2"],
 *  codeName3: [LABEL: "customKey3", PARAM: "attribute3", OPTIONAL : true]]
 * @param result - price parameter data.
 * Must be in following format:
 *  [["customKey1" : "value1",
 *    "customKey2" : "value1",
 *    "customKey3" : "value1"],
 *   ["customKey1" : "value2",
 *    "customKey2" : "value2",
 *    "customKey3" : "value2"]]
 * @return true or false depending whether the data is complete
 */
protected boolean isRequiredDataMissing(Map columnConfig, List<Map> result) {
    Map valuesToCheck = columnConfig.findAll { !it.value.OPTIONAL }

    return result.any { dataRow -> valuesToCheck.any { dataRow[it.value.LABEL] == null } }
}

/**
 * Method that is mapping passed data list of maps according to provided Column config.
 * Mechanism,used to translating map is implemented inside "transformSingleMap"
 * @param dataToTransform - data that we want to process
 * @param columnConfig - column that contains map of submaps which we are using to remap.
 * ex: [TYPE: [LABEL:"type", PARAM:"key1",SOURCE: [LABEL: "source", PARAM: "key2"]
 * Submaps in defined structure are: [LABEL: {new key}, PARAM: {old key}]
 * @return returned
 */
protected List<Map> transformIdKeysIntoMetadataKeys(List dataToTransform, Map columnConfig) {
    return dataToTransform.collect { transformSingleMap(it, columnConfig) }
}

/**
 * Method that is mapping passed data according to provided Column config
 * @param dataToTransform - data that we want to process
 * @param columnConfig - column that contains map of submaps which we are using to remap.
 * Submaps in defined structure are: [LABEL: {new key}, PARAM: {old key}]
 * @return data that is already transformed to new shape
 * Example of behaviour input data :[key1 : value1, key2: value2],
 * and column config: [TYPE: [LABEL:"type", PARAM:"key1"],SOURCE: [LABEL: "source", PARAM: "key2"]]
 * it will return map in this form : [type: value1, source: value2]
 */
protected Map transformSingleMap(def dataToTransform, Map columnConfig) {
    return columnConfig.collect { it.value }
            .collectEntries {
                [(it.LABEL): dataToTransform.("${it.PARAM}" as String)]
            }
}
