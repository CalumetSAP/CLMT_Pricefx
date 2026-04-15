if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

def zbplItem = out.FindZBPL ?: [:]
def zbplCRItem = out.FindZBPLCR ?: [:]

def mergedMap = [:]

(zbplItem.keySet() + zbplCRItem.keySet()).unique().each { key ->
    if (zbplItem.containsKey(key) && zbplCRItem.containsKey(key)) {
        if (zbplCRItem[key].lastUpdateDate >= zbplItem[key]?.lastUpdateDate?.toDate() ) {
            mergedMap[key] = [
                    ProductId        : zbplCRItem[key]?.key5,
                    ValidFrom        : zbplCRItem[key]?.validFrom,
                    ValidTo          : zbplCRItem[key]?.validTo,
                    SalesOrganization: zbplCRItem[key]?.key2,
                    ConditionRecordNo: null,
                    PricelistID      : zbplCRItem[key]?.key4,
                    BasePrice        : zbplCRItem[key]?.conditionValue,
                    Per              : zbplCRItem[key]?.priceUnit,
                    UOM              : zbplCRItem[key]?.unitOfMeasure,
                    Scales           : zbplCRItem[key]?.attribute2,
                    ScaleUOM         : zbplCRItem[key]?.attribute3,
            ]
        } else {
            mergedMap[key] = zbplItem[key]
        }
    } else if (zbplItem.containsKey(key)) {
        mergedMap[key] = zbplItem[key]
    } else if (zbplCRItem.containsKey(key)) {
        mergedMap[key] = [
                ProductId        : zbplCRItem[key]?.key5,
                ValidFrom        : zbplCRItem[key]?.validFrom,
                ValidTo          : zbplCRItem[key]?.validTo,
                SalesOrganization: zbplCRItem[key]?.key2,
                ConditionRecordNo: null,
                PricelistID      : zbplCRItem[key]?.key4,
                BasePrice        : zbplCRItem[key]?.conditionValue,
                Per              : zbplCRItem[key]?.priceUnit,
                UOM              : zbplCRItem[key]?.unitOfMeasure,
                Scales           : zbplCRItem[key]?.attribute2,
                ScaleUOM         : zbplCRItem[key]?.attribute3,
        ]
    }
}

return mergedMap