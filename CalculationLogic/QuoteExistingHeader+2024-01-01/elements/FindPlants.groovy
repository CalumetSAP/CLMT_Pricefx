if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def lineItemSkus = (api.local.lineItemSkus as List ?: [])
if (!lineItemSkus) return []
final skuSet = lineItemSkus as Set

def qapi = api.queryApi()

def t1 = qapi.tables().productExtensionRows(tablesConstants.PRODUCT_EXTENSION_COST)

def pxFilter = qapi.exprs().and(
        t1.sku().in(skuSet as List),
        qapi.exprs().or(
                t1."Attribute 1".isNull(),
                t1."Attribute 1".notEqual("Z1")
        )
)

def plantData = qapi.source(t1, [t1.sku(), t1."Attribute 2"], pxFilter).stream { it.collect() } ?: []
if (!plantData) return []

def plantIds = groupDataByKey(plantData, "sku", "Attribute 2")
if (!plantIds) return []

final allPlantIds = plantIds.values().flatten().findAll { it } as Set
if (!allPlantIds) return []

def t2 = qapi.tables().companyParameterRows(tablesConstants.PLANT)
def plantRows = qapi.source(t2, [t2.key1(), t2."Attribute 1", t2.Status], t2.key1().in(allPlantIds as List)).stream { it.collect() } ?: []

def validSet = plantRows.findAll { it.Status == true }?.collect { it.key1 } as Set
def invalidSet = (allPlantIds - validSet) as Set

def plants = plantRows.collectEntries { [(it.key1): it.key1 + " - " + it."Attribute 1"] } ?: [:]

plantIds.keySet().each { sku ->
    def lst = (plantIds[sku] ?: []).findAll { !(invalidSet.contains(it)) }
    plantIds[sku] = lst.collect { plants[it] ?: it }
}

api.local.plantNames = plants

return plantIds

Map<String, List> groupDataByKey(List data, String groupKey, String valueKey) {
    if (!data) return [:]
    Map<String, List> out = new LinkedHashMap<String, List>().withDefault { new ArrayList(4) }
    for (def e : data) {
        def k = (String) e[groupKey]; if (k == null) continue
        def v = e[valueKey];          if (v == null) continue
        out[k] << v
    }
    return out
}