def indexedItems = []
def totalItems = api.local.rows?.size() ?: 0
def currentItemIndex = 0

def allIndexes = []
final tablesConstants = libs.QuoteConstantsLibrary.Tables

for(row in api.local.rows) {
    if(row?.indexNumberOne && row?.indexNumberOne != "0") allIndexes.add(row?.indexNumberOne)
    if(row?.indexNumberTwo && row?.indexNumberTwo != "0") allIndexes.add(row?.indexNumberTwo)
    if(row?.indexNumberThree && row?.indexNumberThree != "0") allIndexes.add(row?.indexNumberThree)
}
def indexDataMap = [:]

if (!allIndexes.isEmpty()) {
    def indexParts = allIndexes?.findAll{it && it.toString().contains("-")}?.collect { it.split("-") }
    def key1Values = indexParts?.collect {
        try{
            it?.getAt(0)
        }catch(error) {
            api.logWarn("error getting index 0 on it:", it)
        }
    }
    def key2Values = indexParts?.collect {
        try{
            it?.getAt(1)
        }catch(error) {
            api.logWarn("error getting index 1 on it:", it)
        }
    }
    def attribute1Values = indexParts?.collect {
        try{
            it?.getAt(2)
        }catch(error) {
            api.logWarn("error getting index 2 on it:", it)
        }
    }

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

for(row in api.local.rows){
    def newIndexRow = [:]

    def indexes = [row.indexNumberOne, row.indexNumberTwo, row.indexNumberThree] ?: []
    def processedIndexes = []

    for (index in indexes) {
        def foundIndexes = indexDataMap?.get(index)
        if (foundIndexes) {
            def foundIndex = foundIndexes?.first()
            processedIndexes << (foundIndex?.attribute9 + " " + foundIndex?.attribute1)
        }
    }

    processedIndexes = processedIndexes?.join(", ")

    def adder = row?.adder ? api.formatNumber("#####0.0000", row.adder) : ""
    def adderUOM = row?.adderUOM ?: ""
    def adderText = adder && adderUOM ? "a. The adder is ${'$'+adder} / ${adderUOM}" : ""

    //Add 2 break lines only if there are more rows
//    if (currentItemIndex < totalItems - 1) {
//        adderText += "\r\n"
//    }

    newIndexRow["index"] = row["index"]
    newIndexRow["label"] = row["materialAndLabel"] + " : " + row["origin"] + " : " + row["modeOfSale"]
    newIndexRow["indexes"] = processedIndexes
    newIndexRow["refPeriod"] = row["referencePeriod"]
    newIndexRow["recalculationDate"] = row["recalculationDate"] ? addOrdinalSuffix(row["recalculationDate"]?.toInteger()) : ""
    newIndexRow["recalculationPeriod"] = row["recalculationPeriod"]
    newIndexRow["adder"] = adderText

    if(row["index"] && row["index"] != "X"){
        indexedItems << newIndexRow
        currentItemIndex++
    }
}

return indexedItems?.collect { map ->
    def baseText = "Prices for ${map.label} will be based on ${map.indexes} based on a time period of ${map.refPeriod}. " +
            "Prices will change ${map.recalculationDate} of every ${map.recalculationPeriod}"
    def fullText = api.local.showAdder && map.adder ? "${baseText} \n${map.adder}" : baseText
    [
            Index: map.index,
            Text : fullText
    ]
} ?: []

def addOrdinalSuffix(number) {
    def suffixes = ["th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"]
    def special = [11, 12, 13]

    if (special.contains(number % 100)) {
        return "${number}th"
    } else {
        return "${number}${suffixes[number % 10]}"
    }
}