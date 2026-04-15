final tablesConstants = libs.QuoteConstantsLibrary.Tables
final constants = libs.DashboardConstantsLibrary.PLTDashboard

def pricelist = out.Filters?.get(constants.PRICELIST_INPUT_KEY)

def filters = [
        Filter.equal("lookupTable.name", tablesConstants.PRICELIST_FOOTERS),
        Filter.equal("lookupTable.status", "Active"),
        Filter.equal("key1", pricelist)
]
def fields = ["key1", "key2", "attribute1"]

def records = api.stream("MLTV2", null, fields, *filters)?.withCloseable {
    it.collect()?.inject([:]) { acc, item ->
        def key = item.key1
        def list = acc.get(key, [])
        list << [order: item.key2, value: item.attribute1]
        acc[key] = list
        return acc
    }
} ?: [:]

records = records.collectEntries { k, v ->
    [k, v.sort { it.order }.collect { it.value }.join('\n')]
}

api.global.pricelistFooters = records

return null