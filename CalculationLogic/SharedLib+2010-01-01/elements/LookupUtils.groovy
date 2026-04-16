/*
 * This util (also referred to as LookupManager) lets the user unify lookups used at projects. It is meant to be used together with batching,
 * however is possible to run this util only for one item.
 * This util is not specialized into any kind of lookup or batching. Every type of data which api.stream handles can be used.
 * It also does not care which grouping key for batching is being used.
 * Each lookup can be named with a unique key thus allowing to keep multiple lookup results without setting it up each time.
 *
 * This util is a response to some of the most popular requirements for lookups:
 * 1) They need to fetch some data
 * 2) From fetched data, best data must be chosen (with possibility of some fallbacks)
 * 3) If best data is still not good enough, some form of informing the user must be provided and possibly null should be returned
 * 4) For performance reasons, as small number of database calls as possible should be used.
 * 5) Across one project, most rankings for data being perfect/good/OK will be similar (e.g. validity dates, more general contexts)
 *
 * This is how flow, meeting those requirements, goes:
 * 1) User defines on what level he wants batching to work, eg:
 * a) For reading product data from any source (e.g LT/PX), it will be probably "sku"
 * b) For reading customer data from any source, it will usually be "customerId"
 * c) For reading data from any source for segmented customers, it might be ["customerType", "customerClass"] (or other relevant attributesIds)
 * 2) User defines which data must be fetched
 * 3) User defines which checks needs to be performed for data to be ranked. These checks might be of 2 types:
 * a) Individual row level - Checking if data is implicitly good/bad (e.g. given field is not null, current date is in between 2 dates from fields)
 * b) Grouping data set level - Checking if data is relatively good/bad (e.g. only one entry has been fetched for a given key)
 * 4) User executes util
 * 5) User reads resulting data from util
 * 6) User checks results from #3 and handles data appropriately
 * 7) Based of how data has been ranked, user decides if:
 * a) Data should be returned
 * b) Some kind of warning handling should be executed
 *
 *  Example usage in accelerator package:
 * 	<blockquote>
 * 	<pre>
 * def pathToUtil = libs.SharedLib.LookupUtils
 * //#1 defining how batch works
 * String groupingValue = "sku"
 *
 * //#2 defining what data is fetched
 * //(Named parameters are supported)
 * List streamParameters = ["PX", "-ValidFrom", ["sku","Cost", "ValidFrom", "ValidTo", "DependencyLevelName"], Filter.equal("name", "CostData"), Filter.isNotNull("ValidFrom"), Filter.isNotNull("ValidTo")]
 *
 * //#3 defining checks
 * Map VALIDATION_CONTEXTS = pathToUtil.VALIDATION_CONTEXTS
 * Map validators = [:]
 * String today = api.calendar().getTime().format("yyyy-MM-dd")
 * validators << pathToUtil.createValidator("DATE_BETWEEN", VALIDATION_CONTEXTS.ENTRY, { Map lookedUpEntry -> lookedUpEntry.ValidFrom < today && today < lookedUpEntry.ValidTo })
 * validators << pathToUtil.createValidator("HAS_GERMANY_ENTRY", VALIDATION_CONTEXTS.ENTRY_LIST, { List<Map> lookedUpEntries -> lookedUpEntries.any { it.DependencyLevelName == "Germany" } })
 * validators << pathToUtil.createValidator("HAS_EUROPE_ENTRY", VALIDATION_CONTEXTS.ENTRY_LIST, { List<Map> lookedUpEntries -> lookedUpEntries.any { it.DependencyLevelName == "Europe" } })
 * validators << pathToUtil.createValidator("HAS_UNIVERSAL_ENTRY", VALIDATION_CONTEXTS.ENTRY_LIST, { List<Map> lookedUpEntries -> lookedUpEntries.any { it.DependencyLevelName == null } })
 *
 * //#4 execution
 * pathToUtil.lookupData("COST_LOOKUP", groupingValue, validators, *streamParameters)
 *
 * //#5 reading data for current product
 * Map results = pathToUtil.readResults("COST_LOOKUP", api.product("sku"))
 *
 * //#6 and #7 ranking and handling data
 * List<Map> myContextEntries
 * switch (results) {
 *    case { it.HAS_GERMANY_ENTRY }:
 *        myContextEntries = results.data.findAll { Map validatedEntry -> validatedEntry.DependencyLevelName == "Germany" }
 *        break
 *    case { it.HAS_EUROPE_ENTRY }:
 *        myContextEntries = results.data.findAll { Map validatedEntry -> validatedEntry.DependencyLevelName == "Europe" }
 *        break
 *    case { it.HAS_UNIVERSAL_ENTRY }:
 *        myContextEntries = results.data.findAll { Map validatedEntry -> validatedEntry.DependencyLevelName == null }
 *        break
 *    default:
 *        api.throwException("No data for current context")
 *}
 *
 * //Simple find is enough, as we sorted results by sortBy stream's parameter
 * return myContextEntries.find { Map validatedEntry -> validatedEntry.DATE_BETWEEN == true }
 * 	</pre>
 * 	</blockquote>
 *
 * Validating data might be run in context of single entry, or whole group of entries. These contexts are coded to be "ENTRY" and "ENTRY_LIST"
 * Those validators are closures, which expect Map (looked up object) as an input for ENTRY context, or List<Map> for ENTRY_LIST context.
 * Those closures are always expected to return boolean. If they are not, then:
 * a) warning will be raised while looking up data
 * b) warning will be raised while reading any data for that feature
 * c) Closure will be evaluated as false if code thrown exception
 * d) Closure will be evaluated as true, if code returned object with wrong type
 *
 * In terms of reading validators results, all data is kept in Map, which has grouping values as keys.
 * Values of such maps are submaps. That submap has 1 mandatory key, which is "data". Under this key, there is a List of all looked up entries.
 * Beside that, every validator with ENTRY_LIST context is saved in that submap. Entry looks like this [(validatorName):true/false]
 * Validators for ENTRY context, are saved directly on Map representing given entry, next to it's fields (inside data List)
 *
 * Sample return:
 * 	<blockquote>
 * 	<pre>
 * [data: [[sku: "MB-0001", anyEntryValidatorName1: true, anyEntryValidatorName2: false],
 *         [sku: "MB-0002", anyEntryValidatorName1: false, anyEntryValidatorName2: true]],
 *  anyEntryListValidatorName1: true,
 *  anyEntryListValidatorName2: false]
 * 	</pre>
 * 	</blockquote>
 *
 * Some basics usages of util:
 * Example #1, only batching, no validators
 * 	<blockquote>
 * 	<pre>
 *   libs.SharedLib.LookupUtils.lookupData("EXAMPLE", "attribute1", null, "P", "id", Filter.isNotNull("sku"))
 *   Map<String, Map> allBatch = libs.SharedLib.LookupUtils.readBatchedResults("EXAMPLE")
 *   Map currentAndSimilarProducts = libs.SharedLib.LookupUtils.readResults("EXAMPLE", api.product("attribute1"))
 *
 *   assert currentAndSimilarProducts.data.any { Map singleProductEntry -> singleProductEntry.sku == api.product("sku") }
 *   assert allBatch.any { Map.Entry group -> currentAndSimilarProducts == group.value }
 * 	</pre>
 * 	</blockquote>
 *
 *  * Example #2, only batching, standard Price Builder batch usage
 * 	<blockquote>
 * 	<pre>
 *   libs.SharedLib.BatchUtils.prepareBatch(api.product("sku"))
 *   if (libs.SharedLib.BatchUtils.isNewBatch()) {
 *       Filter batchFilter = Filter.in("sku", libs.SharedLib.BatchUtils.getCurrentBatchSku())
 *       Filter pxName = Filter.equal("name", "Cost")
 *       libs.SharedLib.LookupUtils.lookupData("EXAMPLE", "sku", null, "PX", "id", *[batchFilter, pxName])
 *   }
 *   Map currentAndSimilarProducts = libs.SharedLib.LookupUtils.readResults("EXAMPLE", api.product("sku"))
 *
 *   return currentAndSimilarProducts.attribute1?.sum()
 * 	</pre>
 * 	</blockquote>
 *
 *  Example #3, Entry validators
 * 	<blockquote>
 * 	<pre>
 *  Map VALIDATION_CONTEXTS = libs.SharedLib.LookupUtils.VALIDATION_CONTEXTS
 *
 *  Map validators = [:]
 *  validators << libs.SharedLib.LookupUtils.createValidator("STOCK_CHECK", VALIDATION_CONTEXTS.ENTRY, { it.attribute2 * it.attribute3 > 200_000 })
 *
 *  libs.SharedLib.LookupUtils.lookupData("EXAMPLE", "attribute1", validators, "P", "id", Filter.isNotNull("sku"))
 *  Map currentAndSimilarProducts = libs.SharedLib.LookupUtils.readResults("EXAMPLE", api.product("attribute1"))
 *
 *  def result = currentAndSimilarProducts.data.find { Map singleProductEntry -> singleProductEntry.STOCK_CHECK == true }
 *          ?: currentAndSimilarProducts.data[0]
 *
 *  return result
 * 	</pre>
 * 	</blockquote>
 *
 *  Example #4, Entry list validators
 * 	<blockquote>
 * 	<pre>
 *  Map VALIDATION_CONTEXTS = libs.SharedLib.LookupUtils.VALIDATION_CONTEXTS
 *
 *  Map validators = [:]
 *  validators << libs.SharedLib.LookupUtils.createValidator("IS_UNIQUE_PRODUCT", VALIDATION_CONTEXTS.ENTRY_LIST, { it.size() == 1 })
 *
 *  libs.SharedLib.LookupUtils.lookupData("EXAMPLE", "attribute1", validators, "P", "id", Filter.isNotNull("sku"))
 *  Map currentAndSimilarProducts = libs.SharedLib.LookupUtils.readResults("EXAMPLE", api.product("attribute1"))
 *
 *  if (!currentAndSimilarProducts.IS_UNIQUE_PRODUCT) {
 *      createTODOItem(currentAndSimilarProducts.data)
 *  }
 * 	</pre>
 * 	</blockquote>
 *
 *  Example #5, Advance grouping, conditions per group. Classification mechanism is using, that mean one entry can be in several groups
 * 	<blockquote>
 * 	<pre>
 *  Map groupingProperties = ["MB-0001 and quantity >= 20": [[sku: "MB-0001"],Filter.greaterThanOrEqual('attribute2', 20)], //'attribute2' = 'quantity'
 *                            "MB-0001": [sku: "MB-0001"], // sku 'MB-0001' group, regardless quantity
 *                            "Others" : { Map lookedUpEntry -> true } ]
 *
 *  libs.SharedLib.LookupUtils.lookupData("EXAMPLE", groupingProperties, null, "P", "id", Filter.isNotNull("sku"))
 *  Map mb0001WithQuantityGte20 = libs.SharedLib.LookupUtils.readResults("EXAMPLE", "MB-0001 and quantity >= 20").data
 *  Map mb0001 = libs.SharedLib.LookupUtils.readResults("EXAMPLE", "MB-0001").data
 *
 * 	</pre>
 * 	</blockquote>
 */

import com.googlecode.genericdao.search.Filter
import groovy.transform.Field

/**
 * Enum-like structure.
 * We are not using enums, since they are not mockable which makes it harder to test.*/
@Field VALIDATION_CONTEXTS = [ENTRY     : "ENTRY",
                              ENTRY_LIST: "ENTRY_LIST"]

@Field protected NO_GROUPING_KEY = "<<NO_GROUPING_FOR_CACHE>>"

/**
 * Utility method to ensure that entered validators have proper format. Even though these are simple maps, we recommend
 * to use this method to create those
 * Example
 * @param validatorName results of validation will be saved under this name
 * @param validationContext In which context validation should be run. Field VALIDATION_CONTEXTS should be used here
 * @param validationOperation Closure with validation logic
 * @return
 */
Map createValidator(String validatorName, String validationContext, Closure validationOperation) {
    return [(validatorName): [validationContext  : validationContext,
                              validationOperation: validationOperation]]
}

/**
 * Method designed to lookup the date just once per context.
 * Batching, validation and save are equivalent to lookupData(String featureName, def groupingProperties, Map<String, Map> validators, ... streamParameters)
 *
 * @param featureName key to differentiate between batching operations
 * @param groupingProperties fields of looked up objects, on which grouping will be executed
 * When fields are null, everything will be put under one group. Useful for reusing manager for caching purposes (not only batching)
 * @param validators Map build on entries returned from createValidator method
 * @param streamParameters parameters passed to api.stream for lookup
 */
void lookupDataIfEmpty(String featureName, def groupingProperties, Map<String, Map> validators, ... streamParameters) {
    if (api.global.LOOKUP_UTIL?.getAt(featureName) == null) {
        lookupData(featureName, groupingProperties, validators, streamParameters)
    }
}

/**
 * Loads data for whole batch, validates and saves it in api.global.
 * @param featureName key to differentiate between batching operations
 * @param groupingProperties fields of looked up objects, on which grouping will be executed
 * When fields are null, everything will be put under one group. Useful for reusing manager for caching purposes (not only batching)
 * @param validators Map build on entries returned from createValidator method
 * @param streamParameters parameters passed to api.stream for lookup
 */
void lookupData(String featureName, def groupingProperties, Map<String, Map> validators, ... streamParameters) {
    clearWarnings(featureName)
    Map finalData
    def anyException
    try {
        finalData = lookupDataImpl(featureName, groupingProperties, validators, streamParameters)
    } catch (any) {
        anyException = any
    }
    Map filters = [hardFilters: streamParameters as List,
                   softFilters: validators?.keySet()] //Closures are not easy to stringify in sandbox

    saveDataIntoCache(finalData, filters, anyException, featureName)

    return
}

/**
 * Groups, validates, and saves data in api.global for whole batch. It is alternative to lookupData, to be used with already looked up data.
 * Sample use cases:
 * 1) LookupManager does not support SQL queries to DM/DS. Looked up data might be supplied and validated with already implemented validators
 * 2) First step of migrating from standard api.find/api.stream to lookupManager, by implementing validators with already looked up data
 * @param featureName key to differentiate between batching operations
 * @param dataEntries List with already looked up data
 * @param groupingProperties fields of looked up objects, on which grouping will be executed
 * When fields are null, everything will be put under one group. Useful for reusing manager for caching purposes (not only batching)
 * @param validators Map build on entries returned from createValidator method
 */
void feedInput(String featureName, List<Map> dataEntries, def groupingProperties, Map<String, Map> validators) {
    def anyException
    Map groupedData
    try {
        groupedData = groupData(groupingProperties, dataEntries)

        groupedData = validateData(featureName, validators, groupedData)
    } catch (any) {
        anyException = any
    }

    Map filters = [softFilters: validators?.keySet()] //Closures are not easy to stringify in sandbox

    saveDataIntoCache(groupedData, filters, anyException, featureName)

    return
}

/**
 * This function is used for two particular cases that are not natively handled by lookup manager.
 * First case is:
 * Lookup manager does not handle DM/DS input. In this case the data are read manually and loaded via feedInput (for unification purposes).
 * This makes is so if there will be any issues during the data read such information can be passed further to other items.
 * Second case is:
 * Sometimes filters may be invalid (wrong property, no operator) such filters throw exceptions on api.stream without any messages.
 * Such filters should not be passed to the lookup manager as they will sooner or later throw.
 * On the other side, we don't want the thread that reads the LookupManager data to validate two things:
 * whether the data preparation worked and whether the reading worked. So both of these cases are encapsulated into one place.
 * @param featureName key to differentiate between batching operations
 * @param exception thrown on manual lookup or lookup preparation
 */
void feedError(String featureName, Exception exception) {
    clearWarnings(featureName)

    saveDataIntoCache([:], [:], exception, featureName)

    return
}

/**
 * Transforms looked up data in context of single entry.
 * Each group is iterated with inject method. On each entry, entryTransformation is called and returned as replacement
 * @param featureName key to differentiate between batching operations
 * @param entryTransformation Closure to transform Map. Input must be a map and output must be a map or null. If it's null, entry will be filtered out.
 */
void transformEntries(String featureName, Closure<Map> entryTransformation) {
    if (api.global.LOOKUP_UTIL[featureName].lookupError) {
        //Entries are transformed in context of batch, so user will see what went wrong on per-product operations
        return
    }
    Map transformedData = transformEntriesImpl(featureName, entryTransformation)

    api.global.LOOKUP_UTIL[featureName].data = transformedData

    return
}

/**
 * Transforms looked up data in context of all entries in single group.
 * Each group is transformed and overridden by a result of transformation
 * @param featureName key to differentiate between batching operations
 * @param setTransformation Closure to transform List. Input must be a List and output must be a List
 */
void transformDataSet(String featureName, Closure<List> setTransformation) {
    if (api.global.LOOKUP_UTIL[featureName].lookupError) {
        //Entries are transformed in context of batch, so user will see what went wrong on per-product operations
        return
    }

    Map transformedData = transformDataSetImpl(featureName, setTransformation)

    api.global.LOOKUP_UTIL[featureName].data = transformedData

    return
}

/**
 * Revalidates data. Useful is data has been modified by transformations and user want to validate output for convenience.
 * @param featureName key to differentiate between batching operations
 * @param validators Map build on entries returned from createValidator method
 */
void revalidateData(String featureName, Map validators) {
    if (api.global.LOOKUP_UTIL[featureName].lookupError) {
        return
    }

    api.global.LOOKUP_UTIL[featureName].filters.softFilters += validators?.keySet()
    api.global.LOOKUP_UTIL[featureName].data = validateData(featureName,
                                                            validators,
                                                            api.global.LOOKUP_UTIL[featureName].data)
}

/**
 * Reads batch data for all groups. Data is already validated with validators. Debug data is ignored.
 * @param featureName key to differentiate between batching operations
 * @return grouped and validated data from batch.
 */
Map readBatchedResults(String featureName) {
    addSavedWarnings(featureName)

    return api.global.LOOKUP_UTIL?.getAt(featureName)?.data
}

/**
 * Reads bach data for given group. Data is already validated with validators. Debug data is ignored.
 * @param featureName key to differentiate between batching operations
 * @param groupingValues current context values of fields, based on grouping has been performed. Null if no grouping has been performed
 * @return validated group data from batch
 */
Map readResults(String featureName, def groupingValues) {
    addSavedWarnings(featureName)

    return api.global.LOOKUP_UTIL?.getAt(featureName)?.data?.getAt(groupingValues ?: NO_GROUPING_KEY)
}

/**
 * LookupData method will never throw any exception. If exceptions occurs, it will be saved and may be retrieved in this method
 * If there is issue in validating Closure, exception will be handled and api warning will be registered. This method is not for reading those.
 * @param featureName key to differentiate between batching operations
 * @return Exception thrown by code of manager, or null
 */
Exception getBatchingError(String featureName) {
    return api.global.LOOKUP_UTIL[featureName].lookupError
}

/**
 * This method consists only of user entered data about stream parameters and validators. It might be handy during debugging,
 * or retrieving data in runs where batch is utilized (and not run)
 * @param featureName key to differentiate between batching operations
 * @return filters entered by user to lookupData
 */
Map getLookupFilters(String featureName) {
    return api.global.LOOKUP_UTIL[featureName].filters
}

/**
 * This methods allows checking if lookup for a given feature name was already executed.
 * It checks if the structure of the lookup was prepared regardless of any errors and/or validations.
 * Main usecase is verification if a given lookup was already executed for current batch or not.
 * @param featureName key to differentiate between batching operations
 * @return boolean telling if lookup was executed or not
 */
boolean isLookupExecuted(String featureName) {
    return api.global.LOOKUP_UTIL?.getAt(featureName) as boolean
}

protected Map lookupDataImpl(String featureName, def groupingProperties, Map<String, Map> validators, ... streamParameters) {
    def stream = api.stream(*streamParameters)
    List<Map> lookedUpData
    try {
        lookedUpData = stream.collect()
    } catch (any) {
        throw any
    } finally {
        stream.close()
    }

    Map groupedData = groupData(groupingProperties, lookedUpData)

    groupedData = validateData(featureName, validators, groupedData)

    return groupedData
}

protected Map transformEntriesImpl(String featureName, Closure<Map> entryTransformation) {
    return api.global.LOOKUP_UTIL[featureName].data
              .collectEntries { def groupingValue, Map<String, List<Map>> allDataForGroup ->
                  List<Map> entriesForSKU = allDataForGroup.data

                  List transformedEntriesForGroup = getTransformedEntriesForGroup(entriesForSKU, entryTransformation)

                  return [(groupingValue): [data   : transformedEntriesForGroup,
                                            filters: allDataForGroup.filters]]
              }
}

protected List<Map> getTransformedEntriesForGroup(List<Map> entriesForSKU, Closure<Map> entryTransformation) {
    return entriesForSKU.inject([]) { List result, Map singleEntry ->
        Map newEntry = entryTransformation(singleEntry)
        if (newEntry) {
            result << newEntry
        }

        return result
    }
}

protected Map transformDataSetImpl(String featureName, Closure<List> setTransformation) {
    return api.global.LOOKUP_UTIL[featureName].data.collectEntries { def groupingValues, Map<String, List<Map>> allDataForGroup ->
        List<Map> entriesForGroup = allDataForGroup.data

        List<Map> transformedEntriesForGroup = setTransformation(entriesForGroup)

        return [(groupingValues): [data   : transformedEntriesForGroup,
                                   filters: allDataForGroup.filters]]
    }
}

protected Map groupData(def groupingProperties, List<Map> lookedUpData) {
    if (groupingProperties == null) {
        return [(NO_GROUPING_KEY): lookedUpData]
    }

    if (groupingProperties instanceof Map) {
        return classificationGrouping(groupingProperties, lookedUpData)
    }

    Closure groupingFactors = getGroupingClosure(groupingProperties)

    Map groupedData = lookedUpData.groupBy(groupingFactors)

    return groupedData
}

protected Map classificationGrouping(Map groupingProperties, List<Map> lookedUpData) {
    Boolean entryGrouped

    return lookedUpData.inject([:]) { Map groupedData, def lookedUpEntry ->
        entryGrouped = false
        groupingProperties.each { def filterKey, def filterAttempt ->
            if (isLookedUpEntryMatched(filterAttempt, lookedUpEntry)) {
                addToGroup(groupedData, filterKey, lookedUpEntry)
                entryGrouped = true
            }
        }

        if (!entryGrouped) {
            addToGroup(groupedData, NO_GROUPING_KEY, lookedUpEntry)
        }

        return groupedData
    }
}

protected boolean isLookedUpEntryMatched(def filterAttempt, Map lookedUpEntry) {
    def filterUtils = libs.SharedAccLib.FilterUtils

    Filter filter = filterUtils.constructFilter(filterAttempt)
    Closure matcher = { filterUtils.matches(filter, lookedUpEntry) }

    if (!filter && filterAttempt instanceof Closure) {
        matcher = { filterAttempt(lookedUpEntry) }
    }

    return matcher()
}

protected void addToGroup(Map group, def groupingKey, Map lookedUpEntry) {
    group[groupingKey] = group[groupingKey] ?: []
    group[groupingKey] << lookedUpEntry
}

protected void saveDataIntoCache(Map finalData, LinkedHashMap<String, Object> filters, Exception anyException, String featureName) {
    if (api.global.LOOKUP_UTIL == null) {
        api.global.LOOKUP_UTIL = [:]
    }
    api.global.LOOKUP_UTIL[featureName] = [data       : finalData,
                                           filters    : filters,
                                           lookupError: anyException]

    return
}

protected Map validateData(String featureName, Map<String, Map> validators, Map groupedData) {
    Map entryValidators = validators?.findAll { String validationName, Map validationConfig -> validationConfig.validationContext == VALIDATION_CONTEXTS.ENTRY }
    Map setValidators = validators?.findAll { String validationName, Map validationConfig -> validationConfig.validationContext == VALIDATION_CONTEXTS.ENTRY_LIST }

    return groupedData.collectEntries { def groupingValues, List entriesForSet ->
        Map dataSetInfo = [data: entriesForSet]
        dataSetInfo = validateEntries(featureName, dataSetInfo, entryValidators)
        dataSetInfo = validateEntrySet(featureName, dataSetInfo, setValidators)

        return [(groupingValues): dataSetInfo]
    }
}

protected Map validateEntries(String featureName, Map<String, List<Map>> dataSetInfo, Map entryValidators) {
    dataSetInfo.data.each { def dataEntry ->
        entryValidators.each { Map.Entry<String, Map> validator ->
            String validatorName = validator.key

            def validationResult = safeValidate(featureName, validator, dataEntry)

            dataEntry << [(validatorName): validationResult]
        }
    }

    return dataSetInfo
}

protected Map validateEntrySet(String featureName, Map<String, List<Map>> dataSetInfo, Map setValidators) {
    setValidators.each { Map.Entry<String, Map> validator ->
        String validatorName = validator.key

        def validationResult = safeValidate(featureName, validator, dataSetInfo.data)

        dataSetInfo << [(validatorName): validationResult]
    }

    return dataSetInfo
}

protected Boolean safeValidate(String featureName, Map.Entry<String, Map> validator, def input) {
    def validationResult
    try {
        def result = validator.value.validationOperation(input)
        if (result instanceof Boolean) {
            validationResult = result
        } else {
            String warningMessage = "Validator $validator.key did not return Boolean. " +
                    "Default parsing performed " +
                    "Returned object was: [$result]. " +
                    "Input for validator was $input. "
            addWarning(featureName, warningMessage)

            validationResult = result as boolean
        }

    } catch (any) {
        String warningMessage = "Validator $validator.key thrown exception. " + "Evaluated as false. " + "Exception message was: [$any.message]. " + "Input for validator was $input."
        addWarning(featureName, warningMessage)

        validationResult = false
    }

    return validationResult
}

protected Closure getGroupingClosure(groupingProperties) {
    Closure groupingClosure
    if (groupingProperties instanceof String) {
        groupingClosure = { it[groupingProperties] }
    } else if (groupingProperties instanceof List) {
        groupingClosure = { groupingProperties.collect { String groupingProperty -> return it[groupingProperty] } }
    } else {
        api.throwException("Grouping fields has wrong type")
    }

    return groupingClosure
}

protected void addWarning(String featureName, String message) {
    if (api.global.LOOKUP_UTIL_WARNINGS == null) {
        api.global.LOOKUP_UTIL_WARNINGS = [:]
    }
    if (api.global.LOOKUP_UTIL_WARNINGS[featureName] == null) {
        api.global.LOOKUP_UTIL_WARNINGS[featureName] = []
    }
    api.global.LOOKUP_UTIL_WARNINGS[featureName] << message

    api.addWarning(message)

    return
}

protected void addSavedWarnings(String featureName) {
    api.global.LOOKUP_UTIL_WARNINGS?.getAt(featureName)?.each { String warningMessage -> api.addWarning(warningMessage) }

    return
}

protected void clearWarnings(String featureName) {
    if (api.global.LOOKUP_UTIL_WARNINGS == null) {
        api.global.LOOKUP_UTIL_WARNINGS = [:]
    }

    api.global.LOOKUP_UTIL_WARNINGS[featureName] = []

    return
}
