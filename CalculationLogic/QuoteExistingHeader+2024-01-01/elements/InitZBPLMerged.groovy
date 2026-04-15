if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

def zbplItems = out.FindInitZBPL ?: [:]
def zbplCRItems = out.FindInitZBPLCR ?: [:]

def mergedMap = [:]
def zbplItem, zbplCRItem, possibleCRItems
(zbplItems.keySet() + zbplCRItems.keySet()).unique().each { key ->
    if (zbplItems.containsKey(key) && zbplCRItems.containsKey(key)) {
        zbplItem = zbplItems[key]?.max { it.lastUpdateDate }
        zbplCRItem = zbplCRItems[key]?.max { it.lastUpdateDate }
        if (zbplCRItem.lastUpdateDate >= zbplItem?.lastUpdateDate) {
            possibleCRItems = zbplCRItems[key]?.findAll {
                it.attribute4 != "Delete" &&
                        it.attribute5 != "X"
            }
            possibleCRItems?.each {
                mergedMap.putIfAbsent(key, [])
                mergedMap[key].add([
                        ProductId        : it?.key5,
                        ValidFrom        : it?.validFrom?.toString(),
                        ValidTo          : it?.validTo?.toString(),
                        SalesOrganization: it?.key2,
                        ConditionRecordNo: null,
                        PricelistID      : it?.key4,
                        BasePrice        : it?.conditionValue,
                        Per              : it?.priceUnit,
                        UOM              : it?.unitOfMeasure,
                        Scales           : it?.attribute2,
                        ScaleUOM         : it?.attribute3,
                ])
            }
        } else {
            mergedMap[key] = zbplItems[key]
        }
    } else if (zbplItems.containsKey(key)) {
        mergedMap[key] = zbplItems[key]
    } else if (zbplCRItems.containsKey(key)) {
        possibleCRItems = zbplCRItems[key]?.findAll {
            it.attribute4 != "Delete" &&
                    it.attribute5 != "X"
        }
        possibleCRItems?.each {
            mergedMap.putIfAbsent(key, [])
            mergedMap[key].add([
                    ProductId        : it?.key5,
                    ValidFrom        : it?.validFrom?.toString(),
                    ValidTo          : it?.validTo?.toString(),
                    SalesOrganization: it?.key2,
                    ConditionRecordNo: null,
                    PricelistID      : it?.key4,
                    BasePrice        : it?.conditionValue,
                    Per              : it?.priceUnit,
                    UOM              : it?.unitOfMeasure,
                    Scales           : it?.attribute2,
                    ScaleUOM         : it?.attribute3,
            ])
        }
    }
}

return mergedMap