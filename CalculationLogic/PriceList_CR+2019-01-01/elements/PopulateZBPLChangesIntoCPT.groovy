//if(api.isInputGenerationExecution()) return
//
//if (!api.local.isPricelistZBPL) return
//
//def hasChanges = buildHasChangesClosure()
//def changedLines = api.local.plItems?.findAll { hasChanges(it) }
//
//addChangedLines(changedLines)
//
//return null
//
//def addChangedLines(changedLines) {
//    def cptName = libs.QuoteConstantsLibrary.Tables.PRICE_CHANGES_FROM_PL
//    def ppId = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()?.get("id")
//    changedLines?.each { line ->
//        buildRowToAddOrUpdate(ppId, line)
//    }
//}
//
//private def buildRowToAddOrUpdate(ppId, line) {
//    def req = [data: [
//            key1      : line["sku"],
//            key2      : line["Pricelist Number"],
//            key3      : "*",
//            key4      : "*",
//            key5      : line["New Effective Date"],
//            attribute1: false
//    ]]
//
//    def body = api.jsonEncode(req)?.toString()
//
//    def res = api.boundCall("SystemUpdate", "/lookuptablemanager.integrate/" + ppId, body, false)
//}
//
//Closure<Boolean> buildHasChangesClosure() {
//    return { plItem ->
//        if (!eqNum(plItem["New Base Price (ZBPL)"], plItem["Previous Base Price (ZBPL)"])) return true
//
//        if (!eqDate(plItem["New Effective Date"], plItem["Previous Effective Date"])) return true
//
//        if (!eqDate(plItem["New Expiration Date"], plItem["Previous Expiration Date"])) return true
//
//        if (zbplElementsChanged(plItem)) return true
//
//        return false
//    }
//}
//
//private boolean zbplElementsChanged(plItem) {
//    def scalesElements = ["ScaleUOM", "ScaleQty1", "ScaleQty2", "ScaleQty3", "ScaleQty4", "ScaleQty5", "Price1", "Price2", "Price3", "Price4", "Price5", "MOQ", "MOQUOM"]
//    def jobbersElements = ["NewJobberDealerPrice", "NewSRP", "NewMapPrice"]
//
//    return api.jsonDecode(plItem?.manualOverrides as String)?.find { k, v -> scalesElements.contains(v.elementName) || jobbersElements.contains(v.elementName) }
//}
//
//private boolean eqNum(a, b) {
//    if (a == null && b == null) return true
//    if (a == null || b == null) return false
//    new BigDecimal(a.toString()) == new BigDecimal(b.toString())
//}
//
//private boolean eqDate(a, b, boolean ignoreTime = true) {
//    if (a == null && b == null) return true
//    if (a == null || b == null) return false
//    Date da = toDate(a), db = toDate(b)
//    if (da == null || db == null) return a.toString() == b.toString()
//    if (ignoreTime) da.clearTime(); db.clearTime()
//    return da == db
//}
//
//private Date toDate(x) {
//    if (x instanceof Date) return x
//    if (x instanceof CharSequence) {
//        def s = x.toString()
//        for (p in ['yyyy-MM-dd', 'dd/MM/yyyy', 'MM/dd/yyyy', 'yyyy-MM-dd HH:mm:ss']) {
//            try { return Date.parse(p, s) } catch (ignored) {}
//        }
//    }
//    return null
//}