if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final transform = libs.BdpLib.Transform

def lineItemSkus = api.local.lineItemSkus as List

def filter = [
        Filter.equal("name", tablesConstants.PRODUCT_EXTENSION_COST),
        Filter.in("sku", lineItemSkus),
        filterNotEqualZ1Status()]
def fields = ["sku", "attribute2"]
def plantData = api.stream("PX", null, fields, *filter)?.withCloseable { it.collect() }

if (!plantData) return []

def plantIds = transform.groupDataByKey(plantData, "sku", "attribute2")

filter = [
        Filter.in("name", plantIds?.values()?.flatten()),
        Filter.equal("attribute13", true)
]
fields = ["name", "attribute1"]
def plants = api.findLookupTableValues(tablesConstants.PLANT, fields, null, *filter)?.collectEntries { [(it.name): it.name + " - " + it.attribute1] } ?: [:]

filter = [
        Filter.in("name", plantIds?.values()?.flatten()),
        Filter.equal("attribute13", false)
]
fields = ["name"]
def invalidPlants = api.findLookupTableValues(tablesConstants.PLANT, fields, null, *filter)?.collect { it.name } ?: []

plantIds?.each { key, value ->
    if (invalidPlants) value?.removeAll(invalidPlants)
    plantIds[key] = value.collect { plants?.getOrDefault(it, it) }
}

return plantIds

def filterNotEqualZ1Status() {
    return Filter.or(
            Filter.notEqual("attribute1", "Z1"),
            Filter.equal("attribute1", ""),
            Filter.isNull("attribute1")
    )
}