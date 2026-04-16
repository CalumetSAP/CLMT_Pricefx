if (api.isInputGenerationExecution()) return null

def quote = api.currentItem()

if (quote?.get("quoteType") != "ExistingContractUpdate") return null

def indexedItems = []
def totalItems = quote?.lineItems?.size() ?: 0
def currentItemIndex = 0

def uomDescription = api.global.uomDescription

def indexCounter = 0
def getNextIndex = {
    def alphabet = ('A'..'Z').join()
    def result = ""
    def num = indexCounter
    while (num >= 0) {
        result = alphabet[num % 26] + result
        num = (num / 26).intValue() - 1
    }
    indexCounter++
    return result
}

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def allIndexes = []

for (line in quote?.lineItems) {
    if (!line.folder) {
        if (calculations.getInputValue(line, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)) allIndexes.addAll(calculations.getInputValue(line, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID))
    }
}

def indexDataMap = [:]
if (!allIndexes.isEmpty()) {
    def indexParts = allIndexes.collect { it.split("-") }
    def key1Values = indexParts.collect { it[0] }
    def key2Values = indexParts.collect { it[1] }
    def attribute1Values = indexParts.collect { it[2] }

    def indexFilters = [
            Filter.equal("lookupTable.name", tablesConstants.INDEX_VALUES),
            Filter.equal("lookupTable.status", "Active"),
            Filter.in("key1", key1Values),
            Filter.in("key2", key2Values),
            Filter.in("attribute1", attribute1Values)
    ]

    def indexDataList = api.findLookupTableValues(tablesConstants.INDEX_VALUES, ["key1", "key2", "key4", "attribute1", "attribute9"], null, *indexFilters)
    indexDataMap = indexDataList.groupBy { "${it.key1}-${it.key2}-${it.attribute1}"?.toString() }
}

for (line in quote?.lineItems) {
    if (line.folder) {
        continue
    }

    def priceType = calculations.getInputValue(line, lineItemConstants.PRICE_TYPE_ID)
    priceType = priceType ? getPriceTypeValues()?.get(priceType) : priceType
    if (priceType != "1") {
        continue
    }

    def material = line.sku ?: ""
    def label = line.label ?: ""

    def indexes = calculations.getInputValue(line, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID) ?: []
    def processedIndexes = []

    for (index in indexes) {
        def foundIndexes = indexDataMap[index]
        if (foundIndexes) {
            def foundIndex = foundIndexes.first()
            processedIndexes << (foundIndex.attribute9 + " " + foundIndex.attribute1)
        }
    }
    processedIndexes = processedIndexes?.join(", ")

    def refPeriod = calculations.getInputValue(line, lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)

//    def includeAdders = quote.get("inputs").find { it.name == "InputsConfigurator" }?.value?.IncludeAdderInput
    def includeAdders = true
    def adder = calculations.getInputValue(line, lineItemConstants.PF_CONFIGURATOR_ADDER_ID) ? api.formatNumber("#####0.0000", calculations.getInputValue(line, lineItemConstants.PF_CONFIGURATOR_ADDER_ID)) : ""
    def pricingUomInput = calculations.getInputValue(line, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID) ?: ""
    def pricingUom = uomDescription?.get(pricingUomInput) ? uomDescription?.get(pricingUomInput) : pricingUomInput ?: ""
    def adderText = adder && includeAdders ? "The adder is ${'$'+adder} / ${pricingUom}" : ""

    //Add 2 break lines only if there are more rows
    if (currentItemIndex < totalItems - 1) {
        adderText += "\r\n\r\n"
    }

    def recalculationDate = calculations.getInputValue(line, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID) ?: ""
    def recalculationPeriod = calculations.getInputValue(line, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID) ?: ""

    def index = getNextIndex()

    def item = [
            index: index,
            material: material,
            label: label,
            indexes: processedIndexes,
            refPeriod: refPeriod,
            adder: adderText,
            pricingUom: pricingUom,
            recalculationDate: recalculationDate ? addOrdinalSuffix(recalculationDate?.toInteger()) : "",
            recalculationPeriod: recalculationPeriod
    ]

    indexedItems << item
    currentItemIndex++
}

return indexedItems

def addOrdinalSuffix(number) {
    def suffixes = ["th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"]
    def special = [11, 12, 13]

    if (special.contains(number % 100)) {
        return "${number}th"
    } else {
        return "${number}${suffixes[number % 10]}"
    }
}

def getPriceTypeValues() {
    def tablesConstants = libs.QuoteConstantsLibrary.Tables

    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.in("key2", ["PriceType"]),
    ]
    return api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)?.collectEntries {
        [(it.attribute1) : it.key3]
    }
}
