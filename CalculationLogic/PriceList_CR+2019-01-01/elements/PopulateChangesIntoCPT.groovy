if(api.isInputGenerationExecution()) return

if (!api.local.isMassEdit && !api.local.isPricingFormula) return

def attrConfig
if (api.local.isMassEdit) {
    attrConfig = [
            priceNew          : ["New Price"],
            priceOld          : ["Current Price"],
            datePairs         : [
                    ["Old Valid From", "Effective Date"],
                    ["Old Valid To", "Expiration Date"]
            ],
            previousScaleField: "Previous Scales"
    ]
} else {
    attrConfig = [
            priceNew          : ["New Price"],
            priceOld          : ["Current Price"],
            datePairs         : [],
            previousScaleField: null
    ]
}

def hasChanges = buildHasChangesClosure(attrConfig)
def changedLines = api.local.plItems?.findAll { hasChanges(it) }

addChangedLines(changedLines)

return null

def addChangedLines(changedLines) {
    def cptName = libs.QuoteConstantsLibrary.Tables.PRICE_CHANGES_FROM_PL
    def ppId = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()?.get("id")
    def today = new Date()
    changedLines?.each { line ->
        buildRowToAddOrUpdate(ppId, line, today)
    }
}

private def buildRowToAddOrUpdate(ppId, line, today) {
    def req = [data: [
            key1      : line["sku"],
            key2      : "*",
            key3      : line["Contract"],
            key4      : line["Contract Item"],
            key5      : line["Effective Date"],
            attribute1: false,
            attribute2: today
    ]]

    def body = api.jsonEncode(req)?.toString()

    def res = api.boundCall("SystemUpdate", "/lookuptablemanager.integrate/" + ppId, body, false)
}

Closure<Boolean> buildHasChangesClosure(Map config) {
    return { plItem ->
        if (!eqNum(getField(plItem, config.priceNew as List<String>), getField(plItem, config.priceOld as List<String>))) return true

        for (pair in config.datePairs) {
            if (!eqDate(plItem[pair[0]], plItem[pair[1]])) return true
        }

        if (config.previousScaleField && scalesChanged(plItem, config)) return true

        return false
    }
}

private def getScales(plItem) {
    def scaleQtyAux, priceAux
    def scales = []
    def numberOfDecimals = plItem["Number of Decimals"] ? plItem["Number of Decimals"]?.toInteger() : 2
    for (int i = 1; i < 6; i++) {
        scaleQtyAux = plItem["Scale Qty ${i}"]
        priceAux = libs.SharedLib.RoundingUtils.round(plItem["Price ${i}"], numberOfDecimals)
        if (scaleQtyAux && priceAux) {
            scales.add([
                    scaleQty: scaleQtyAux,
                    price: priceAux
            ])
        }
    }
    scales.sort { it.scaleQty }
    return scales.collect { "${it.scaleQty}=${it.price}" }.join("|")
}

private def getField(beanOrMap, List<String> names) {
    def v
    for (n in names) {
        v = (beanOrMap instanceof Map) ? beanOrMap[n] : beanOrMap."$n"
        if (v != null) return v
    }
    return null
}

private boolean scalesChanged(plItem, Map sCfg) {
    def current = getScales(plItem)
    def previous = plItem[sCfg.previousScaleField as String]
    return current != previous
}

private boolean eqNum(a, b) {
    if (a == null && b == null) return true
    if (a == null || b == null) return false
    new BigDecimal(a.toString()) == new BigDecimal(b.toString())
}

private boolean eqDate(a, b, boolean ignoreTime = true) {
    if (a == null && b == null) return true
    if (a == null || b == null) return false
    Date da = toDate(a), db = toDate(b)
    if (da == null || db == null) return a.toString() == b.toString()
    if (ignoreTime) da.clearTime(); db.clearTime()
    return da == db
}

private Date toDate(x) {
    if (x instanceof Date) return x
    if (x instanceof CharSequence) {
        def s = x.toString()
        for (p in ['yyyy-MM-dd', 'dd/MM/yyyy', 'MM/dd/yyyy', 'yyyy-MM-dd HH:mm:ss']) {
            try { return Date.parse(p, s) } catch (ignored) {}
        }
    }
    return null
}