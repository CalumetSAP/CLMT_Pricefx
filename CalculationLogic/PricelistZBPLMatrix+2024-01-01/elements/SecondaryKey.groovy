import java.time.LocalDate

def secKey = []

def sku = api.local.sku
def salesOrgs = api.global.salesOrgs
def pls = api.global.pricelists
def zbplDSItems = out.LoadZBPL
def zbplCRItems = out.LoadZBPLCR
def newProducts = api.global.products
def plsNewItems = api.global.pricelistsNewItems
def effectiveDate = api.global.effectiveDate

Calendar cal = Calendar.getInstance();
cal.setTime(api.global.effectiveDate as Date);

LocalDate effectiveLocalDate = LocalDate.of(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH)
)

def zbplDS, zbplCR, possibleItems
for (salesOrg in salesOrgs) {
    for (pl in pls) {
        zbplDS = zbplDSItems.get([sku, salesOrg, pl])?.max { it.lastUpdateDate }
        zbplCR = zbplCRItems.get([salesOrg, pl, sku])?.max { it.lastUpdateDate }
        possibleItems = null
        if (zbplCR && zbplCR.lastUpdateDate > zbplDS?.lastUpdateDate) {
            possibleItems = zbplCRItems?.get([salesOrg, pl, sku])?.find {
                it.attribute4 != "Delete" &&
                        it.attribute5 != "X" &&
                        it.validFrom <= effectiveLocalDate &&
                        it.validTo >= effectiveLocalDate
            }
        } else if (zbplDS) {
            possibleItems = zbplDSItems?.get([sku, salesOrg, pl])?.find {
                        it.ValidFrom <= effectiveDate &&
                        it.ValidTo >= effectiveDate
            }
        }
        if (possibleItems) { //Item exists in DS or CR
            secKey.add(salesOrg + "-" + pl)
        }
    }

    if (newProducts?.contains(sku)) { //If it is New Item
        for (pl in plsNewItems) {
            secKey.add(salesOrg + "-" + pl)
        }
    }
}

return secKey?.unique()