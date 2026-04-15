import com.googlecode.genericdao.search.Filter

// full
def all(String typeCode, String sortBy, List<String> fields, Boolean distinctValuesOnly, Filter... filters) {
	def start = 0
	def limit = api.getMaxFindResultsLimit()
	def out = []
	def getFindArgs = { ->
		fields == null
				? [typeCode, start, limit, sortBy, filters]
				: [typeCode, start, limit, sortBy, fields, distinctValuesOnly, filters]
	}
	while (data = api.find(*getFindArgs())) {
		out.addAll(data)
		start += data.size()
	}
	return out
}

// without distinct
def all(String typeCode, String sortBy, List<String> fields, Filter... filters) {
	return all(typeCode, sortBy, fields, false, filters)
}

// without distinct, sortBy
def all(String typeCode, List<String> fields, Filter... filters) {
	return all(typeCode, null, fields, false, filters)
}

// without distinct, fields
def all(String typeCode, String sortBy, Filter... filters) {
	return all(typeCode, sortBy, null, false, filters)
}

// without sortBy
def all(String typeCode, List<String> fields, Boolean distinctValuesOnly, Filter... filters) {
	return all(typeCode, null, fields, distinctValuesOnly, filters)
}

// without sortBy, fields
def all(String typeCode, Boolean distinctValuesOnly, Filter... filters) {
	return all(typeCode, null, null, distinctValuesOnly, filters)
}

// without fields
def all(String typeCode, String sortBy, Boolean distinctValuesOnly, Filter... filters) {
	return all(typeCode, sortBy, null, distinctValuesOnly, filters)
}

// without distinct, sortBy, fields
def all(String typeCode, Filter... filters) {
	return all(typeCode, null, null, false, filters)
}