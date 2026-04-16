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
            "MOQ/UOM"                                    : it.moqUom,
            "Price"                                      : it.price,
            "Price UOM"                                  : it.priceUOM,
            "Incoterms / Freight"                        : it.freight,
            "QUANTITY"                                   : null,
            "EXTENDED PRICE"                             : null,
    ]
    newRowList.add(row)
}

return newRowList