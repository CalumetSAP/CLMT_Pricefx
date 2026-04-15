def zlisItem = out.LoadZLIS
def zlisCRItem = out.LoadZLISCR
if (zlisCRItem && zlisCRItem.lastUpdateDate > zlisItem?.lastUpdateDate) {
    return [
            Price       : zlisCRItem.conditionValue,
            UOM         : zlisCRItem.unitOfMeasure,
            Currency    : zlisCRItem.currency,
    ]
} else {
    return [
            Price       : zlisItem?.Amount,
            UOM         : zlisItem?.UOM,
            Currency    : zlisItem?.Currency,
    ]
}