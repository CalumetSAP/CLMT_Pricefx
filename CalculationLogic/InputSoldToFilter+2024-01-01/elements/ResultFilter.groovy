if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def data = api.jsonDecode(filterFormulaParam)
if (data.SelectedSoldTos) return Filter.in("customerId", data.SelectedSoldTos)

def shouldUseBlockFilter = data.ShouldUseBlockFilter

def cxName = tablesConstants.CUSTOMER_EXTENSION_PARTNER_FUNCTION

def blockFilter = Filter.and(
        Filter.or(
                Filter.isNull("attribute10"),
                Filter.isEmpty("attribute10")
        ),
        Filter.or(
                Filter.isNull("attribute11"),
                Filter.isEmpty("attribute11")
        ),
        Filter.or(
                Filter.isNull("attribute13"),
                Filter.isEmpty("attribute13")
        )
)

def filters = []
filters.add(Filter.in("${cxName}__attribute4", ["SP", "AG"]))
if (shouldUseBlockFilter) filters.add(blockFilter)

def validSoldTo = api.stream("C", null, ["customerId"], Filter.and(*filters)).withCloseable {
    it.collect { it.customerId }
}.unique()

def shipToFilter = Filter.and(
        Filter.equal("name", cxName),
        Filter.in("customerId", validSoldTo),
        Filter.in("attribute4", ["PY", "BP"])
)
def findShipTos = api.stream("CX", null, ["customerId", "attribute1", "attribute4"], shipToFilter).withCloseable {
    it.collect().groupBy { it.customerId }
} ?: [:]

def filteredMap = findShipTos.findAll { key, valueList ->
    def hasPY = valueList.any { it.attribute4 == "PY" }
    def hasBP = valueList.any { it.attribute4 == "BP" }
    hasPY && hasBP
}
def possibleShipTos = filteredMap.values()
        .flatten()
        .collect { it.attribute1 }
        .unique()

def shipToListFilter = []
shipToListFilter.add(Filter.in("customerId", possibleShipTos))
if (shouldUseBlockFilter) shipToListFilter.add(blockFilter)

def validShipTo = api.stream("C", null, ["customerId"], Filter.and(*shipToListFilter)).withCloseable {
    it.collect { it.customerId }
}.unique()

def finalValidSoldTo = filteredMap.findAll { key, valueList ->
    def hasValidPY = valueList.any { it.attribute4 == "PY" && validShipTo.contains(it.attribute1) }
    def hasValidBP = valueList.any { it.attribute4 == "BP" && validShipTo.contains(it.attribute1) }
    hasValidPY && hasValidBP
}?.keySet()?.toList()

return Filter.in("customerId", finalValidSoldTo)