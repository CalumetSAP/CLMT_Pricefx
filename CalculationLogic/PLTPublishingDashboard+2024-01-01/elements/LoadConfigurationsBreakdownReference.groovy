final tablesConstants = libs.QuoteConstantsLibrary.Tables

def materials = api.global.materials

def filters = [
        Filter.equal("lookupTable.name", tablesConstants.CONFIGURATIONS_BREAKDOWN_REFERENCE),
        Filter.equal("lookupTable.status", "Active"),
        Filter.in("key1", materials)
]

def fields = ["key1", "key2", "attribute1"]

def records = api.stream("MLTV2", null, fields, *filters)?.withCloseable {
    it.collect()?.inject([:]) { acc, item ->
        def key = item.key1
        def value = item.key2 + " - " + item.attribute1
        acc[key] = (acc[key] ?: []) << value
        return acc
    }
} ?: [:]

api.global.configurationBreakdownReference = records

return null