import java.text.SimpleDateFormat

if (api.isInputGenerationExecution() || !InputPricelist?.input?.getValue() || (InputPriceType.input?.getValue() != "3" && InputPriceType.input?.getValue() != "2")) return [:]

def sdf = new SimpleDateFormat("yyyy-MM-dd")

def zbplItems = out.FindBasePricing
def zbplCRItems = out.FindZBPLCR ?: []

def effectiveDate = InputPriceValidFrom?.input?.getValue()
def zbplItem, zbplCRItem, possibleCRItems
if (zbplItems && zbplCRItems) {
    zbplItem = zbplItems?.max { it.lastUpdateDate }
    zbplCRItem = zbplCRItems?.max { it.lastUpdateDate }
    if (zbplCRItem.lastUpdateDate >= zbplItem?.lastUpdateDate?.toDate()) {
        possibleCRItems = zbplCRItems?.findAll {
            it.attribute4 != "Delete" &&
                    it.attribute5 != "X"
        }
        zbplCRItem = possibleCRItems?.max { it.validFrom }
        return [
                ProductId        : zbplCRItem?.key5,
                ValidFrom        : zbplCRItem?.validFrom,
                ValidTo          : zbplCRItem?.validTo,
                SalesOrganization: zbplCRItem?.key2,
                ConditionRecordNo: null,
                PricelistID      : zbplCRItem?.key4,
                BasePrice        : zbplCRItem?.conditionValue,
                Per              : zbplCRItem?.priceUnit,
                UOM              : zbplCRItem?.unitOfMeasure,
                Scales           : zbplCRItem?.attribute2,
                ScaleUOM         : zbplCRItem?.attribute3,
        ]
    } else {
        return zbplItems?.max { it.ValidFrom }
    }
} else if (zbplItems) {
    return zbplItems?.max { it.ValidFrom }
} else if (zbplCRItems) {
    possibleCRItems = zbplCRItems?.findAll {
        it.attribute4 != "Delete" &&
                it.attribute5 != "X"
    }
    zbplCRItem = possibleCRItems?.max { it.validFrom }
    return [
            ProductId        : zbplCRItem?.key5,
            ValidFrom        : zbplCRItem?.validFrom,
            ValidTo          : zbplCRItem?.validTo,
            SalesOrganization: zbplCRItem?.key2,
            ConditionRecordNo: null,
            PricelistID      : zbplCRItem?.key4,
            BasePrice        : zbplCRItem?.conditionValue,
            Per              : zbplCRItem?.priceUnit,
            UOM              : zbplCRItem?.unitOfMeasure,
            Scales           : zbplCRItem?.attribute2,
            ScaleUOM         : zbplCRItem?.attribute3,
    ]
}

return [:]