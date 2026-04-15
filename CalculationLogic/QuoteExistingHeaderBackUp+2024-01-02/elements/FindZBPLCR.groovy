if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.basePricingCRFilters) return

List fields = ["key2", "key3", "key4", "key5", "validFrom", "validTo", "unitOfMeasure", "priceUnit",
               "conditionValue", "currency", "attribute2", "attribute3", "lastUpdateDate"]

List filters = [
        Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A932"]),
        Filter.equal("key1", "ZBPL"),
        Filter.notEqual("attribute4", "Delete")
]

def customFilter = Filter.or(*api.local.basePricingCRFilters)

filters.add(customFilter)

def rowIterator = api.stream("CRCI5", "-lastUpdateDate", fields, *filters)
def zbplCondRecMap = [:]
rowIterator?.each { row ->
    zbplCondRecMap.putIfAbsent(row.key2 + "|" + row.key4 + "|" + row.key5, row)
}
rowIterator?.close()

return zbplCondRecMap