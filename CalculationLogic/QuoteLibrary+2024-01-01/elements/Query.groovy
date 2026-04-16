def findFullNamesByUser(List loginNames) {
    def fields = ["loginName", "firstName", "lastName"]

    def usersMap = api.find("U", 0, api.getMaxFindResultsLimit(), null, fields, null)?.collectEntries {[
            (it.loginName.toString().toLowerCase()): [
                    LoginName: it.loginName,
                    FullName : (it.firstName ?: "") + (it.lastName ? " " + it.lastName : "")
            ]
    ]}

    return loginNames.collect { usersMap.get(it.toString().toLowerCase()) }
}

def findEmailByUser(loginName) {
    def filter = Filter.equal("loginName", loginName)
    def fields = ["email"]

    return api.find("U", 0, api.getMaxFindResultsLimit(), null, fields, filter)?.find()?.email
}

def findFreightValues(freightFilters) {
    if (!freightFilters) return
    final tablesConstants = libs.QuoteConstantsLibrary.Tables

    def ctx = api.getDatamartContext()
    def dataSource= ctx.getDataSource(tablesConstants.DATA_SOURCE_QUOTES)

    def customFilter = Filter.or(*freightFilters)

    def query = ctx.newQuery(dataSource, false)
    query.identity {
        select("SAPContractNumber", "SAPContract")
        select("SAPLineID", "LineNumber")
        select("FreightAmount", "FreightAmount")
        select("FreightValidFrom", "FreightValidFrom")
        select("FreightValidto", "FreightValidTo")
        select("FreightUOM", "FreightUOM")
        select("lastUpdateDate", "lastUpdateDate")
        select("QuoteLastUpdate", "QuoteLastUpdate")

        where(customFilter)
        orderBy("QuoteLastUpdate DESC")
        selectDistinct()
    }

    def result = ctx.executeQuery(query)
    def map = [:]
    result?.getData()?.each {
        map.putIfAbsent(it.SAPContract + "|" + it.LineNumber, it)
    }

    return map
}

def findFreightValues(Filter freightFilters, Map pvfByKey) {
    if (!freightFilters) return
    final tablesConstants = libs.QuoteConstantsLibrary.Tables

    def ctx = api.getDatamartContext()
    def dataSource= ctx.getDataSource(tablesConstants.DATA_SOURCE_QUOTES)

    def query = ctx.newQuery(dataSource, false)
    query.identity {
        select("SAPContractNumber", "SAPContract")
        select("SAPLineID", "LineNumber")
        select("Material", "Material")
        select("FreightAmount", "FreightAmount")
        select("FreightValidFrom", "FreightValidFrom")
        select("FreightValidto", "FreightValidTo")
        select("FreightUOM", "FreightUOM")
        select("lastUpdateDate", "lastUpdateDate")
        select("QuoteLastUpdate", "QuoteLastUpdate")

        where(freightFilters)
        orderBy("QuoteLastUpdate DESC")
    }

    def rows = ctx.executeQuery(query)?.getData() ?: []
    def bestByKey = [:]
    rows?.each { r ->
        def fullKey = "${r.SAPContract}|${r.LineNumber}|${r.Material}"
        def pvf = pvfByKey[fullKey]
        if (!pvf) return

        if (!isDateAfter(r.FreightValidFrom, pvf) && !isDateAfter(pvf, r.FreightValidTo)) {
            def k = r.SAPContract + "|" + r.LineNumber
            bestByKey.putIfAbsent(k, r)
        }
    }

    return bestByKey
}

private boolean isDateAfter(def a, def b, boolean ignoreTime = true) {
    if (a == null || b == null) return false
    Date da = toDate(a)
    Date db = toDate(b)
    if (da == null || db == null) return false
    if (ignoreTime) da.clearTime(); db.clearTime()
    return da.after(db)
}

private Date toDate(def x) {
    if (x instanceof Date) return x
    if (x instanceof CharSequence) {
        def s = x.toString()
        for (p in ['yyyy-MM-dd', 'dd/MM/yyyy', 'MM/dd/yyyy', 'yyyy-MM-dd HH:mm:ss']) {
            try { return Date.parse(p, s) } catch (ignored) {}
        }
    }
    return null
}

List getQuoteScalesRows (lineIds) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("QuoteScales")

    def query = ctx.newQuery(dm, false)
            .selectAll(true)
            .setUseCache(false)
            .where(Filter.in("LineID", lineIds))

    return ctx.executeQuery(query)?.getData()?.collect() ?: []
}

Map getZBPLScales (conditionRecords) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZBPLScales")

    def query = ctx.newQuery(dm, false)
            .select("ConditionRecordNo", "ConditionRecordNo")
            .select("ScaleQuantity", "ScaleQty")
            .select("ConditionRate", "Price")
            .select("lastUpdateDate", "lastUpdateDate")
            .where(Filter.in("ConditionRecordNo", conditionRecords))
            .orderBy("ScaleQuantity")

    return ctx.executeQuery(query)?.getData()?.groupBy {it.ConditionRecordNo }?: [:]
}

List getAllDeliveredFreightTerms() {
    def tablesConstants = libs.QuoteConstantsLibrary.Tables

    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.equal("key2", "FreightTerm"),
            Filter.equal("attribute2", true),
    ]

    return api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)?.collect { it.key3 }
}