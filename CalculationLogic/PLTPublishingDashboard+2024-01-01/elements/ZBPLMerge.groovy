import java.text.SimpleDateFormat

final constants = libs.DashboardConstantsLibrary.PLTDashboard

def sdf = new SimpleDateFormat("yyyy-MM-dd")

def configurator = out.Filters
String pricingDate = configurator?.get(constants.PRICING_DATE_INPUT_KEY)

def zbplItems = api.global.zbpl ?: [:]
def zbplCRItems = api.global.zbplCR ?: [:]

def mergedMap = [:]
def zbplItem, zbplCRItem, possibleCRItems, possibleItems
(zbplItems.keySet() + zbplCRItems.keySet()).unique().each { key ->
    zbplItem = zbplItems.get(key)?.max { it.lastUpdateDate }
    zbplCRItem = zbplCRItems.get(key)?.max { it.lastUpdateDate }
    if (zbplCRItem &&
            zbplCRItem.lastUpdateDate >= zbplItem?.lastUpdateDate?.toDate() &&
            zbplCRItem.lastUpdateDate >= api.global.ZBPLScales.get(zbplItem?.ConditionRecordNo)?.max { it.lastUpdateDate?.toDate() }?.lastUpdateDate?.toDate()) {
        possibleCRItems = zbplCRItems.get(key).findAll {
            it.attribute4 != "Delete" &&
                    it.attribute5 != "X" &&
                    it.validFrom <= sdf.parse(pricingDate) &&
                    it.validTo >= sdf.parse(pricingDate)
        }
        zbplCRItem = possibleCRItems?.max { it.lastUpdateDate }
        if (!zbplCRItem) return

        mergedMap[key] = [
                Material         : zbplCRItem?.key5,
                ValidFrom        : zbplCRItem?.validFrom,
                ValidTo          : zbplCRItem?.validTo,
                SalesOrganization: zbplCRItem?.key2,
                ConditionRecordNo: null,
                Pricelist        : zbplCRItem?.key4,
                Amount           : zbplCRItem?.conditionValue,
                Per              : zbplCRItem?.priceUnit,
                UnitOfMeasure    : zbplCRItem?.unitOfMeasure,
                Scales           : zbplCRItem?.attribute2,
                ScaleUoM         : zbplCRItem?.attribute3,
                ConditionCurrency: zbplCRItem?.currency,
        ]
    } else {
        possibleItems = zbplItems?.get(key)?.findAll {
            it.ValidFrom <= sdf.parse(pricingDate) &&
                    it.ValidTo >= sdf.parse(pricingDate)
        }
        zbplItem = possibleItems?.max { it.lastUpdateDate }
        if (!zbplItem) return

        mergedMap[key] = zbplItem
    }
}

api.global.ZBPLmerge = mergedMap
api.global.materials = mergedMap?.values()?.collect { it.Material } ?: []

return null