def newRowList = []
def row
api.local.sortedRows?.each {
    row = [
            "Product Code"                               : it.material,
            "Description"                                : it.materialLabel,
            "Legacy Part No."                            : it.legacyPartNo,
            "Customer Material Number / Additional Notes": it.customerMaterialNumber,
            "Origin"                                     : it.origin,
            "Delivered Location"                         : it.deliveredLocation,
            "Effective Date"                             : it.effectiveDate,
            "MOQ/UOM"                                    : it.moqUom,
            "Price"                                      : it.price,
            "Price UOM"                                  : it.priceUOM,
            "Incoterms / Freight"                        : it.freight,
    ]
    if (api.local.hasJobbers) row.put("Jobbers/Dealer Price / EA", it.jobbers)
    if (api.local.hasSRP) row.put("SRP / EA", it.srp)
    if (api.local.hasMAP) row.put("MAP / EA", it.map)
    newRowList.add(row)
}

return newRowList