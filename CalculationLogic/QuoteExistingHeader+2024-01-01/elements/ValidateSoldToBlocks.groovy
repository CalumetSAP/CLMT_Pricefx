if (api.isInputGenerationExecution() || !quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def invalidSoldToList = []

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def getSoldToValues = customerConfigurator?.get(headerConstants.SOLD_TO_VALUES_HIDDEN_ID)
if (!getSoldToValues) return

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

def filters = Filter.and(
        blockFilter,
        Filter.in("customerId", getSoldToValues)
)

def validSoldTo = api.stream("C", null, ["customerId"], filters).withCloseable {
    it.collect { it.customerId }
}.unique()

def invalidSoldTos = getSoldToValues - validSoldTo

if (invalidSoldTos) {
    invalidSoldToList.addAll(invalidSoldTos)
}

def filter = Filter.and(
        Filter.equal("name", cxName),
        Filter.in("customerId", validSoldTo),
        Filter.in("attribute4", ["PY", "BP"])
)
def findShipTos = api.stream("CX", null, ["customerId", "attribute1", "attribute4"], filter).withCloseable {
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

def shipToFilter = Filter.and(
        Filter.in("customerId", possibleShipTos),
        blockFilter
)

def validShipTo = api.stream("C", null, ["customerId"], shipToFilter).withCloseable {
    it.collect { it.customerId }
}.unique()

def finalValidSoldTo = filteredMap.findAll { key, valueList ->
    def hasValidPY = valueList.any { it.attribute4 == "PY" && validShipTo.contains(it.attribute1) }
    def hasValidBP = valueList.any { it.attribute4 == "BP" && validShipTo.contains(it.attribute1) }
    hasValidPY && hasValidBP
}?.keySet()?.toList()

def invalidSoldTosFromShipTos = getSoldToValues - finalValidSoldTo

if (invalidSoldTosFromShipTos) {
    invalidSoldToList.addAll(invalidSoldTosFromShipTos)
}

invalidSoldTosFromShipTos.unique()

api.local.invalidSoldTos = invalidSoldTosFromShipTos.join(", ")

return null