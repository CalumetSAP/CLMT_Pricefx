import java.util.regex.Matcher
import java.util.regex.Pattern

/**

 * Deprecated use <b>createDictionaryFromList</b>
 * Converts a list of maps to a key-value map.
 *
 * @param data a list of maps where each map represents a row of data
 * @param key a string that represents the key to use as the new map's key
 * @param value a string that represents the key to use as the new map's value
 * @return a new map where the keys are the values in the `key` field of each map in `data`, and the
 *         values are the values in the `value` field of each map in `data`.
 */
@Deprecated
def mapListOfMapsToKeyedValues(data, String key, String value) {
	data.inject([:], { acc, row ->
		acc[row[key] as String] = row[value]
		return acc
	})
}

/**
 * <b>Deprecated use groupDataByKeys</b>
 * Groups the values in a list of maps by a specified group key.
 *
 * @param data The list of maps to group values from.
 * @param groupKey The key to group the values by.
 * @param valueKey The key to extract the values from in each map.
 * @return A map containing the grouped values.
 */
@Deprecated
def groupValuesByGroupKey(data, String groupKey, String valueKey) {
	data.inject([:]) { formatted, entry ->
		String key = entry[groupKey]
		def value = entry[valueKey]
		formatted[key] = formatted.containsKey(key) ? formatted[key] + value : [value]
		formatted
	}
}

/**
 * Groups the data by the concatenated keys in `groupKeys` and returns the grouped values.
 *
 <pre>
 // Example usage
 <b>With Stream</b>
 api.global.costDetailsByCostComponent = api.stream("MLTV3", null, fields, *filters)
 .withCloseable {aggregateDataByKeyFields(it.collect(), ["key1", "key2"], "-", ["key3"])}<b>Mock Data</b>
 def data = [
 [key1: 25, key2: 'product1', key3: 'AA1'],
 [key1: 25, key2: 'product1', key3: 'AA2',],
 [key1: 25, key2: 'product2', key3: 'AA1'],
 [key1: 30, key2: 'product1', key3: 'AAA'],
 [key1: 30, key2: 'product1, key3: 'BBB']
 [key1: 30, key2: 'product1, key3: 'CCC']
 ]

 def groupKeys = ['key1', 'key2']
 def charKeySeparator = '_'
 def groupedValuesKeys = ['key3']

 def groupedData = groupValuesByConcatenatedKeys(data, groupKeys, charKeySeparator, groupedValuesKeys)

 assert groupedData == [
 '25_product1': [[key3: 'AA1'], [key3: 'AA2']],
 '25_product2': [[key3: 'AA1']],
 '30_product1': [[key3: 'AAA'], [key3: 'BBB'], [key3: 'CCC']],
 ]
 </pre>
 *
 * @param data a list of maps where each map represents a row of data
 * @param groupKeys a list of strings that represents the keys to group the data by
 * @param charKeySeparator a string that represents the separator to use when concatenating the keys
 * @param groupedValuesKeys a list of strings that represents the keys to group the values by
 * @return a map where the keys are the concatenated group keys and the values are a list of maps
 *         where each map represents a row of data that matches the concatenated group key.
 *
 */
@Deprecated
def aggregateDataByKeyFields(data, List<String> groupKeys, String charKeySeparator, List<String> groupedValuesKeys) {
	data.inject([:], { acc, row ->
		String key = groupKeys.inject(null, { concatenatedKey, rowKey ->
			concatenatedKey ? concatenatedKey + charKeySeparator + row[rowKey] : row[rowKey]
		})
		def rowValues = groupedValuesKeys.collectEntries { [(it): row[it]] }
		acc[key] = acc[key] ? [*acc[key], rowValues].unique(false) : [rowValues]
		return acc
	})
}

/**
 Creates a dictionary/map from a list of objects based on the specified key and value properties.
 <pre>
 // Example usage
 def data = [
 [id: 1, name: 'Alice'],
 [id: 2, name: 'Bob'],
 [id: 3, name: 'Charlie']
 ]

 def keyValueMap = listMapToKeyValueMap(data, 'id', 'name')

 assert keyValueMap == [1: 'Alice', 2: 'Bob', 3: 'Charlie']
 assert access == keyValueMap[1] == 'Alice'
 </pre>
 @param data a list of objects to be used to create the dictionary/map
 @param key the name of the property to be used as the key in the resulting dictionary/map
 @param value the name of the property to be used as the value in the resulting dictionary/map
 @return a dictionary/map where the keys are the values of the specified key property, and the values are the values of the specified value property
 */
@Deprecated
def createDictionaryFromList(List<Object> data, String key, String value) {
	return data.collectEntries { [(it[key]): it[value]] }
}

/**
 * Groups the values in a list of maps by a specified group key.
 *
 * @param data The list of maps to group values from.
 * @param groupKey The key to group the values by.
 * @param valueKey The key to extract the values from in each map.
 * @return A map containing the grouped values.
 */
@Deprecated
def groupDataByKey(List<Object> data, String groupKey, String valueKey) {
	data.inject([:]) { formatted, entry ->
		String key = entry[groupKey]
		def value = entry[valueKey]
		formatted[key] = formatted.containsKey(key) ? formatted[key] + value : [value]
		formatted
	}
}

/**
 * Groups the data by the concatenated keys in `groupKeys` and returns the grouped values.
 *
 <pre>
 // Example usage
 <b>With Stream</b>
 api.global.costDetailsByCostComponent = api.stream("MLTV3", null, fields, *filters)
 .withCloseable {aggregateDataByKeyFields(it.collect(), ["key1", "key2"], "-", ["key3"])}<b>Mock Data</b>
 def data = [
 [key1: 25, key2: 'product1', key3: 'AA1'],
 [key1: 25, key2: 'product1', key3: 'AA2',],
 [key1: 25, key2: 'product2', key3: 'AA1'],
 [key1: 30, key2: 'product1', key3: 'AAA'],
 [key1: 30, key2: 'product1, key3: 'BBB']
 [key1: 30, key2: 'product1, key3: 'CCC']
 ]

 def groupKeys = ['key1', 'key2']
 def charKeySeparator = '_'
 def groupedValuesKeys = ['key3']

 def groupedData = groupValuesByConcatenatedKeys(data, groupKeys, charKeySeparator, groupedValuesKeys)

 assert groupedData == [
 '25_product1': [[key3: 'AA1'], [key3: 'AA2']],
 '25_product2': [[key3: 'AA1']],
 '30_product1': [[key3: 'AAA'], [key3: 'BBB'], [key3: 'CCC']],
 ]
 </pre>
 *
 * @param data a list of maps where each map represents a row of data
 * @param groupKeys a list of strings that represents the keys to group the data by
 * @param charKeySeparator a string that represents the separator to use when concatenating the keys
 * @param groupedValuesKeys a list of strings that represents the keys to group the values by
 * @return a map where the keys are the concatenated group keys and the values are a list of maps
 *         where each map represents a row of data that matches the concatenated group key.
 *
 */
def groupDataByMultipleKeys(data, List<String> groupKeys, String charKeySeparator, List<String> groupedValuesKeys) {
	data.inject([:], { acc, row ->
		String key = groupKeys.inject(null, { concatenatedKey, rowKey ->
			concatenatedKey ? concatenatedKey + charKeySeparator + row[rowKey] : row[rowKey]
		})
		def rowValues = groupedValuesKeys.collectEntries { [(it): row[it]] }
		acc[key] = acc[key] ? [*acc[key], rowValues].unique(false) : [rowValues]
		return acc
	})
}

/**
 * Groups the data by the concatenated keys in `groupKeys` and returns the grouped values.
 *
 <pre>
 // Example usage
 <b>With Stream</b>
 api.global.costDetailsByCostComponent = api.stream("MLTV3", null, fields, *filters)
 .withCloseable {aggregateDataByKeyFields(it.collect(), ["key1", "key2"], "-", "key3")}<b>Mock Data</b>
 def data = [
 [key1: 25, key2: 'product1', key3: 'AA1'],
 [key1: 25, key2: 'product1', key3: 'AA2',],
 [key1: 25, key2: 'product2', key3: 'AA1'],
 [key1: 30, key2: 'product1', key3: 'AAA'],
 [key1: 30, key2: 'product1, key3: 'BBB']
 [key1: 30, key2: 'product1, key3: 'CCC']
 ]

 def groupKeys = ['key1', 'key2']
 def charKeySeparator = '_'
 def groupedValuesKeys = 'key3'

 def groupedData = groupValuesByConcatenatedKeys(data, groupKeys, charKeySeparator, groupedValuesKeys)

 assert groupedData == [
 '25_product1': key3,
 '25_product2': key3,
 '30_product1': key3,
 ]
 </pre>
 *
 * @param data a list of maps where each map represents a row of data
 * @param groupKeys a list of strings that represents the keys to group the data by
 * @param charKeySeparator a string that represents the separator to use when concatenating the keys
 * @param groupedValuesKeys a list of strings that represents the keys to group the values by
 * @return a map where the keys are the concatenated group keys and the values are a list of maps
 *         where each map represents a row of data that matches the concatenated group key.
 *
 */
def groupSingleValueByMultipleKeys(data, List<String> groupKeys, String charKeySeparator, String groupedValueKey) {
	data.inject([:], { acc, row ->
		String key = groupKeys.inject(null, { concatenatedKey, rowKey ->
			concatenatedKey ? concatenatedKey + charKeySeparator + row[rowKey] : row[rowKey]
		})
		acc[key] = row[groupedValueKey]
		return acc
	})
}

def singleKeyToDictionary(List<Object> data, String key) {
	if (!data || !key) return null
	return data.collectEntries { row -> [(row[key]): row.findAll { field -> field.key != key }] }
}

def singleKeyToDictionary(List<Object> data, String key, String field) {
	if (!data || !key || !field) return null
	return data.collectEntries { row -> [(row[key]): row[field]] }
}

def singleKeyToDictionary(List<Object> data, String key, List<String> fields) {
	if (!data || !key || !fields) return null
	return data.collectEntries { row -> [(row[key]): row.findAll { field -> fields.contains(field.key) }] }
}

def singleKeyToDictionaryAggregation(data, String key) {
	data.inject([:]) { formatted, entry ->
		String keyField = entry[key]
		def value = entry.findAll { field -> field.key != key }
		formatted[keyField] = formatted.containsKey(keyField) ? formatted[keyField] + value : [value]
		formatted
	}
}

def singleKeyToDictionaryAggregation(data, String key, String field) {
	data.inject([:]) { formatted, entry ->
		String keyField = entry[key]
		def value = entry[field]
		formatted[keyField] = formatted.containsKey(keyField) ? formatted[keyField] + value : [value]
		formatted
	}
}

/**
 * <p><b>Deprecated use splitPairKeyConcatenation()</b></p>
 *
 * Splits the code string based (XXX-XXXX) on the provided separator and validates the code format.
 * The code string must not be null, and the separator must not be null.
 * The code string must contain exactly one occurrence of the separator.
 *
 * @param code The code string to split and validate.
 * @param separator The separator used to split the code string.
 * @return A map containing the extracted keys from the code string.
 * @throws Exception if the code string is null, the separator is null, or the code format is invalid.
 *
 * @example
 * // Example usage:
 * def code = "123-asd"
 * def separator = "-"
 * def keys = splitCodeAndValidateFormat(code, separator)
 * println("Key 1: ${keys.key1}") // Output: Key 1: asd
 * println("Key 2: ${keys.key2}") // Output: Key 2: 123
 */
@Deprecated
def splitCodeAndValidateFormat(String code, String separator) {
	if (!code) api.throwException("code is null")
	if (!separator) api.throwException("separator is null")

	def splitedCode = code?.split(separator)
	if (splitedCode.size() != 2) api.throwException("Invalid code format: must contain only one separator")

	def key1 = splitedCode?.last()
	def key2 = splitedCode?.first()

	return [key1: key1, key2: key2]
}

/**
 * Splits a pair key using a separator and returns a Map containing the split parts.
 *
 * @param pairKey The input pair key to be split.
 * @param separator The separator used to split the pair key.
 * @return A Map containing the split parts(first & last), or null if the input is invalid.
 * @throws Exception if the input is invalid or contains more than one separator.
 */
Map<String, String> splitPairKeyConcatenation(String pairKey, String separator) {
	if (!pairKey || pairKey.isEmpty()) return null
	if (!separator || separator.isEmpty()) api.throwException("splitKeyConcatenation Error, separator is required!")

	// Check if the pairKey contains the separator, if not, return a Map with "first" key
	if (!pairKey.find(separator)) return [first: pairKey]

	// Use Pattern to split the pairKey using the provided separator (treated as a regex)
	Pattern pattern = Pattern.compile(separator)
	def splitCode = pattern.split(pairKey)

	// Check if there are more than two parts after splitting
	if (splitCode.length > 2) {
		api.throwException("BdpLib.Transform.splitPairKeyConcatenation: More than one separator found in pairKey: " + pairKey)
	}

	if (splitCode.length == 1) {
		Matcher matcher = pattern.matcher(pairKey)

		String last = matcher.find() && matcher.end() == pairKey.length()
				? ""
				: splitCode.find()

		String first = matcher.start() == 0
				? ""
				: splitCode.find()

		return [first: first, last: last]
	}
	return [first: splitCode?.first(), last: splitCode?.last()]
}