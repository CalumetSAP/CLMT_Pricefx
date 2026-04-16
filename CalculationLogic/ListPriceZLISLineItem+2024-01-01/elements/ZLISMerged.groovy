def zlisItem = out.LoadZLIS
def zlisCRItem = out.LoadZLISCR

if(!zlisCRItem && !zlisItem) {
    api.local.isNewProduct = true
}

if (zlisCRItem && zlisCRItem.lastUpdateDate > zlisItem?.lastUpdateDate?.toDate()) {
    return [
            ValidFrom   : zlisCRItem.validFrom,
            ValidTo     : zlisCRItem.validTo,
            Price       : zlisCRItem.conditionValue,
            UOM         : zlisCRItem.unitOfMeasure,
            Per         : zlisCRItem.priceUnit,
            Currency    : zlisCRItem.currency,
    ]
} else {
    return [
            ValidFrom   : zlisItem?.Valid_From,
            ValidTo     : zlisItem?.Valid_To,
            Price       : zlisItem?.Amount,
            UOM         : zlisItem?.UOM,
            Per         : zlisItem?.Per,
            Currency    : zlisItem?.Currency,
    ]
}