import java.text.SimpleDateFormat

if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

if (!api.local.addedContracts) return

def basePricingCRFilters = []

def contracts = out.FindContractDSData
contracts?.each { contractNumber, lines ->
    lines?.each { line ->
        def pricelist = line?.PriceListPLT
        if (pricelist && line?.PriceValidFrom && line?.PriceValidTo && line?.Material) {
            basePricingCRFilters.add(Filter.and(
                    Filter.lessOrEqual("validFrom", line?.PriceValidFrom?.toString()),
                    Filter.greaterOrEqual("validTo", line?.PriceValidFrom?.toString()),
                    Filter.equal("key5", line?.Material),
                    Filter.equal("key4", pricelist)
            ))
        }
    }
}

if (!basePricingCRFilters) return

List fields = ["key2", "key3", "key4", "key5", "validFrom", "validTo", "unitOfMeasure", "priceUnit",
               "conditionValue", "currency", "attribute2", "attribute3", "lastUpdateDate"]

List filters = [
        Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A932"]),
        Filter.equal("key1", "ZBPL"),
        Filter.notEqual("attribute4", "Delete")
]

def customFilter = Filter.or(*basePricingCRFilters)

filters.add(customFilter)

def rowIterator = api.stream("CRCI5", "-lastUpdateDate", fields, *filters)
def zbplCondRecMap = [:]
rowIterator?.each { row ->
    zbplCondRecMap.putIfAbsent(row.key2 + "|" + row.key4 + "|" + row.key5, row)
}
rowIterator?.close()

return zbplCondRecMap