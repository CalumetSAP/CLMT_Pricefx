import java.time.LocalDate

def effectiveDate = api.local.newEffectiveDate

Calendar cal = Calendar.getInstance();
cal.setTime(api.local.newEffectiveDate as Date);

LocalDate effectiveLocalDate = LocalDate.of(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH)
)

def zbplItems = out.LoadZBPL
def zbplScalesItem = out.LoadZBPLScales
def zbplCRItems = out.LoadZBPLCR ?: []

def zbplItem, possibleItems, zbplCRItem, possibleCRItems
zbplCRItem = zbplCRItems?.max { it.lastUpdateDate }
zbplItem = zbplItems?.max { it.lastUpdateDate }
if (zbplCRItem && zbplCRItem.lastUpdateDate > zbplItem?.lastUpdateDate && zbplCRItem.lastUpdateDate > zbplScalesItem.max { it.lastUpdateDate }?.lastUpdateDate) {
    possibleCRItems = zbplCRItems?.findAll {
        it.attribute4 != "Delete" &&
                it.attribute5 != "X" &&
                it.validFrom <= effectiveLocalDate &&
                it.validTo >= effectiveLocalDate
    }
    zbplCRItem = possibleCRItems?.max { it.lastUpdateDate }
    if (!zbplCRItem) return [:]
    return [
            DistributionChannel : zbplCRItem.key3,
            ValidFrom           : zbplCRItem.validFrom,
            ValidTo             : zbplCRItem.validTo,
            Price               : zbplCRItem.conditionValue,
            UOM                 : zbplCRItem.unitOfMeasure,
            ScaleUoM            : zbplCRItem.attribute3,
            Per                 : zbplCRItem.priceUnit,
            Currency            : zbplCRItem.currency,
            Scales              : zbplCRItem.attribute2 ? mapZBPLCRScales(zbplCRItem.attribute2) : null
    ]
} else {
    possibleItems = zbplItems?.findAll {
        it.ValidFrom <= effectiveDate &&
                it.ValidTo >= effectiveDate
    }
    zbplItem = possibleItems?.max { it.lastUpdateDate }
    if (!zbplItem) return [:]
    return [
            DistributionChannel : zbplItem?.DistributionChannel,
            ValidFrom           : zbplItem?.ValidFrom,
            ValidTo             : zbplItem?.ValidTo,
            Price               : zbplItem?.Amount,
            UOM                 : zbplItem?.UnitOfMeasure,
            ScaleUoM            : zbplItem?.ScaleUoM,
            Per                 : zbplItem?.Per,
            Currency            : zbplItem?.ConditionCurrency,
            Scales              : zbplScalesItem
    ]
}

List mapZBPLCRScales (crScales) {
    def scale
    return crScales?.split("\\|")?.collect {
        scale = it?.split("=")
        return [
                ScaleQuantity: scale[0]?.toBigDecimal() ?: null,
                ConditionRate: scale[1]?.toBigDecimal() ?: null
        ]
    } ?: []
}