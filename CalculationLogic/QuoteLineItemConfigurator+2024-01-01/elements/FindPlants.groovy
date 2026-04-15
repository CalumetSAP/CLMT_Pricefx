if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def filter = [
        Filter.equal("name", tablesConstants.PRODUCT_EXTENSION_COST),
        Filter.equal("sku", api.local.sku),
        filterNotEqualZ1Status()]
def fields = ["sku", "attribute2"]
def plantData = api.stream("PX", null, fields, *filter)?.withCloseable { it.collect() }

if (!plantData) return []

def plantIds = plantData?.attribute2

filter = [
        Filter.in("name", plantIds),
        Filter.equal("attribute13", true)
]
fields = ["name", "attribute1"]
def plants = api.findLookupTableValues(tablesConstants.PLANT, fields, null, *filter)?.collectEntries { [(it.name): it.name + " - " + it.attribute1] } ?: [:]

filter = [
        Filter.in("name", plantIds),
        Filter.equal("attribute13", false)
]
fields = ["name"]
def invalidPlants = api.findLookupTableValues(tablesConstants.PLANT, fields, null, *filter)?.collect { it.name } ?: []

if (invalidPlants) plantIds?.removeAll(invalidPlants)

return plantIds?.collect { plants?.getOrDefault(it, it) }

def filterNotEqualZ1Status() {
    return Filter.or(
            Filter.notEqual("attribute1", "Z1"),
            Filter.equal("attribute1", ""),
            Filter.isNull("attribute1")
    )
}