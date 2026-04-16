final tablesConstants = libs.QuoteConstantsLibrary.Tables
final constants = libs.DashboardConstantsLibrary.PricePublishing

def configurator = out.Filters
def variantName = configurator[constants.VARIANT_INPUT_KEY] ?: configurator[constants.VARIANT_NAME_INPUT_KEY]

def filters = [
        Filter.equal("lookupTable.name", tablesConstants.DASHBOARD_FOOTERS),
        Filter.equal("lookupTable.status", "Active"),
        Filter.equal("key1", variantName),
        Filter.equal("key2", "30"),
]
def fields = ["key1", "key2", "key3", "attribute1"]

def records = api.stream("MLTV3", null, fields, *filters)?.withCloseable {
    it.collect()?.inject([:]) { acc, item ->
        def key = item.key1
        def list = acc.get(key, [])
        list << [order: item.key3, value: item.attribute1]
        acc[key] = list
        return acc
    }
} ?: [:]

records = records.collectEntries { k, v ->
    [k, v.sort { it.order }]
}

api.global.footers = records

return null