def zbplItem = out.LoadZBPL
def zbplScalesItem = out.LoadZBPLScales
def zbplCRItem = out.LoadZBPLCR
if (zbplCRItem && zbplCRItem.lastUpdateDate > zbplItem?.lastUpdateDate?.toDate() && zbplCRItem.lastUpdateDate > zbplScalesItem.lastUpdateDate?.toDate()) {
    api.local.zbplScalesMerged = out.LoadZBPLScalesCR
    return [
            DistributionChannel : zbplCRItem.key3,
            Price               : zbplCRItem.conditionValue,
            UOM                 : zbplCRItem.attribute3,
            Per                 : zbplCRItem.priceUnit,
            Currency            : zbplCRItem.currency,
    ]
} else {
    api.local.zbplScalesMerged = zbplScalesItem
    return [
            DistributionChannel : zbplItem?.DistributionChannel,
            Price               : zbplItem?.Amount,
            UOM                 : zbplItem?.UnitOfMeasure,
            Per                 : zbplItem?.Per,
            Currency            : zbplItem?.ConditionCurrency,
    ]
}