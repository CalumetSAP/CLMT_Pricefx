def getInputsFromPricelistId(plId) {
    if(api.isDebugMode()){
        def pl = api.find("PL", Filter.equal("id",plId))
        return api.jsonDecode(pl?.configuration)?.formulaParameters
    }
}

def getAllPLItems(plId){
    def start = 0
    def maxRows = api.getMaxFindResultsLimit()
    def items = []
    def filters = Filter.equal("pricelistId", plId as String)
    while ((plItems = api.find("XPLI", start, maxRows, "id", filters))) {
        start += plItems.size()
        items.addAll(plItems)
    }
    items = api.namedEntities(items)
    return items
}

def getExclusions (soldTos, extraFilter = null) {
    List<String> fields = ["key1", "key2", "key3", "key4", "attribute1", "attribute2", "attribute3", "attribute4", "attribute5", "attribute6"]
    def filters = [Filter.in("key2", soldTos)]
    if (extraFilter) {
        filters.add(extraFilter)
    }
    //TODO move hardcoded table name to Constants
    return api.findLookupTableValues("Exclusions", fields, null, *filters)?.groupBy { it.key2 }?.collectEntries { soldTo, values1 ->
        [soldTo, values1?.groupBy { it.key3 }?.collectEntries { shipTo, values2 ->
            [shipTo, values2?.groupBy { it.key1 }?.collectEntries { ph1, values3 ->
                [ph1, values3?.groupBy { it.key4 }?.collectEntries { material, values4 ->
                    [material, values4]
                }]
            }]
        }]
    }
}