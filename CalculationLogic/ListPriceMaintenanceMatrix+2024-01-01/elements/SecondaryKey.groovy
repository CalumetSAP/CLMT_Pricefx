def secKey = []

def sku = api.local.sku
def salesOrgs = api.global.salesOrgs
def pls = api.global.pricelists
def plsNewItems = api.global.pricelistsNewItems
def scalesNewItems = api.global.scalesNewItems
List<String> lineNumbers = libs.PricelistLib.Constants.ZBPL_SCALES_LINE_NUMBERS
def secKeyAux = []
def zbpl, zbplCR, zbplScales, conditionRecordNo, scaleSize, scaleLineNumbers
for (salesOrg in salesOrgs) {
    if (api.global.products?.contains(sku)) { //If it is New Item
        for (pl in plsNewItems) {
            if (scalesNewItems) {
                for (scales in scalesNewItems) {
                    secKeyAux.add(salesOrg + "-" + pl + "-" + scales)
                }
            } else {
                secKeyAux.add(salesOrg + "-" + pl)
            }
        }
    } else {
        for (pl in pls) {
            zbpl = out.LoadZBPL.get([sku, salesOrg, pl])
            zbplCR = out.LoadZBPLCR.get([salesOrg, pl, sku])
            conditionRecordNo = zbpl?.ConditionRecordNo
            zbplScales = out.LoadZBPLScales.get(conditionRecordNo)
            if (zbplCR && zbplCR.lastUpdateDate > zbpl?.lastUpdateDate?.toDate() && zbplCR.lastUpdateDate > zbplScales?.lastUpdateDate?.toDate()) {
                scaleSize = zbplCR?.attribute2?.split("\\|")?.size() ?: 0
                scaleLineNumbers = lineNumbers.take(scaleSize)
                if (scaleLineNumbers) {
                    for (lineNumber in scaleLineNumbers) {
                        secKeyAux.add(salesOrg + "-" + pl + "-" + lineNumber) //ZBPL CR with scale
                    }
                } else {
                    secKeyAux.add(salesOrg + "-" + pl) // ZBPL CR with no scales
                }
            } else {
                if (conditionRecordNo) {
                    scaleLineNumbers = zbplScales?.scaleLineNumbers
                    if (scaleLineNumbers) {
                        for (lineNumber in scaleLineNumbers) {
                            secKeyAux.add(salesOrg + "-" + pl + "-" + lineNumber) //ZBPL with scale
                        }
                    } else {
                        secKeyAux.add(salesOrg + "-" + pl) // ZBPL with no scales
                    }
                }
            }
        }
    }

    if (secKeyAux.isEmpty()) {
        //If (there are not ZBPL, not scales, not new items and ZLIS or ZLIS CR exists) OR (is a new item without pl and scales)
        if (out.LoadZLIS.get([sku, salesOrg]) || out.LoadZLISCR.get([salesOrg, sku]) || api.global.products?.contains(sku)) {
            secKey.add(salesOrg) // Only ZLIS
        }
    } else {
        secKey.addAll(secKeyAux.unique())
        secKeyAux = []
    }

}

return secKey?.unique()