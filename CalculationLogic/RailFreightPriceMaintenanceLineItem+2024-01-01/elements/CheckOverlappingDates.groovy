import java.text.SimpleDateFormat

def plId = api.global.pricelistId

if (api.global.isFirstRow && plId) {
    api.global.plItems = getPLItems(plId)
}

def errors = []

def items = api.global.plItems?.get([out.ContractNumber, out.ContractLine])

if (items) {
    def newPriceValidFrom = api.getManualOverride("NewPriceValidFrom") ?: out.NewPriceValidFrom
    def newPriceValidTo = api.getManualOverride("NewPriceValidTo") ?: out.NewPriceValidTo
    def newFreightValidFrom = api.getManualOverride("NewFreightValidFrom") ?: out.NewFreightValidFrom
    def newFreightValidTo = api.getManualOverride("NewFreightValidTo") ?: out.NewFreightValidTo

    items = replaceOverriddenValues(items, api.local.secondaryKey, newPriceValidFrom, newPriceValidTo, newFreightValidFrom, newFreightValidTo)

    def priceOverlap = checkOverlapping(items, "NewPriceValidFrom", "NewPriceValidTo")
    def freightOverlap = checkOverlapping(items, "NewFreightValidFrom", "NewFreightValidTo")

    if (priceOverlap) errors.add("Price Valid dates overlap with other line")
    if (freightOverlap) errors.add("Freight Valid dates overlap with other line")
}

if (out.NewPriceValidFrom > out.NewPriceValidTo) {
    errors.add("New Price Valid From can not be greater than New Price Valid To")
}

if (out.NewFreightValidFrom > out.NewFreightValidTo) {
    errors.add("New Freight Valid From can not be greater than New Freight Valid To")
}

api.local.overlappingErrors = errors

def replaceOverriddenValues(List items, secondaryKey, newPriceValidFrom, newPriceValidTo, newFreightValidFrom, newFreightValidTo) {
    return items.collect {
        if (secondaryKey == it.SecondaryKey) {
            if (newPriceValidFrom) it["NewPriceValidFrom"] = newPriceValidFrom
            if (newPriceValidTo) it["NewPriceValidTo"] = newPriceValidTo
            if (newFreightValidFrom) it["NewFreightValidFrom"] = newFreightValidFrom
            if (newFreightValidTo) it["NewFreightValidTo"] = newFreightValidTo
            return it
        } else {
            return it
        }
    }
}

boolean checkOverlapping(List itms, String fromField, String toField) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

    def ranges = itms
            .findAll { it[fromField] && it[toField] }
            .collect { [
                    from: it[fromField] instanceof String ? sdf.parse(it[fromField]) : it[fromField] as Date,
                    to: it[toField] instanceof String ? sdf.parse(it[toField]) : it[toField] as Date
            ] }
            .sort { a, b -> a.from <=> b.from }

    for (int i = 1; i < ranges.size(); i++) {
        def prev = ranges[i - 1]
        def curr = ranges[i]

        if (!curr.from.after(prev.to)) {
            return true
        }
    }

    return false
}

Map getPLItems(plId) {
    List elementNames = ["ContractNumber", "ContractLine", "NewPriceValidFrom", "NewPriceValidTo", "NewFreightValidFrom", "NewFreightValidTo"]

    Map<String, String> elementToFieldNameMap = getElementToFieldMap(plId, elementNames)
    if (!elementToFieldNameMap) return [:]

    List plFilter = [
            Filter.equal("pricelistId", plId)
    ]

    return api.stream("XPLI", null, ["sku", "key2"] + elementToFieldNameMap.values().toList(), *plFilter)?.withCloseable {
        it.collect { buildPLItem(elementNames, elementToFieldNameMap, it)}.groupBy {[it.ContractNumber, it.ContractLine]}
    } ?: [:]
}

Map<String, String> getElementToFieldMap(plId, elementsNames) {
    List attributeMappingFilters = [
            Filter.equal("pricelistId", plId),
            Filter.in("elementName", elementsNames)
    ]
    return api.find("PLIM", 0, 100, null, ["elementName", "fieldName"], *attributeMappingFilters)
            ?.collectEntries { [(it.elementName): it.fieldName] }
}

Map buildPLItem(List<String> elementNames, Map<String, String> elementToFieldNameMap, element) {
    Map result = [:]
    for (elementName in elementNames) {
        result[elementName] = element[elementToFieldNameMap[elementName]]
    }
    result["SecondaryKey"] = element.key2
    return result
}