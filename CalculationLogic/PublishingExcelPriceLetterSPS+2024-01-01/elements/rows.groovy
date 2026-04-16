def newRowList = []
def row, splitMOQ, splitPrice
api.local.sortedRows?.each {
    splitMOQ = splitByChar(it.moqUom, "/")
    splitPrice = splitByChar(it.priceUom, "/")
    row = [
            "Sales Rep Name"                             : it.salesRepName,
            "Product Code"                               : it.material,
            "Description"                                : it.materialLabel,
            "Customer Material Number / Additional Notes": it.customerMaterialNumber,
            "Origin"                                     : it.origin,
            "Delivered Location"                         : it.deliveredLocation,
            "Mode Of Sale"                               : it.modeOfSale,
            "Effective Date"                             : it.effectiveDate,
            "WPG"                                        : it.wpg,
            "Min Qty"                                    : splitMOQ.first,
            "UoM"                                        : splitMOQ.second,
            "Price"                                      : splitPrice.first,
            "Price UoM"                                  : splitPrice.second,
            "Incoterms / Freight"                        : it.freight,
    ]
    if (out.hasIndexedItems) row.put("Indexed", it.index)
    newRowList.add(row)
}

return newRowList

def splitByChar(String input, String separator) {
    def part1, part2

    if (input?.contains(separator)) {
        def parts = input.split(separator, 2)
        part1 = parts[0].trim()
        part2 = parts[1].trim()
    } else {
        part1 = input?.trim()
        part2 = null
    }

    return [first: part1, second: part2]
}
