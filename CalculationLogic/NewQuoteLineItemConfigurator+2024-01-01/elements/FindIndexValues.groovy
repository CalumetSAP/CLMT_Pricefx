if (api.isInputGenerationExecution() || InputPriceType.input?.getValue() != "1") return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def filters = [
        Filter.equal("lookupTable.name", tablesConstants.INDEX_VALUES),
        Filter.equal("lookupTable.status", "Active"),
]

return api.stream("MLTV4", null, ["key1", "key2", "attribute1"], true, *filters)?.collect {
    it.key1 + "-" + it.key2 + "-" + it.attribute1
}