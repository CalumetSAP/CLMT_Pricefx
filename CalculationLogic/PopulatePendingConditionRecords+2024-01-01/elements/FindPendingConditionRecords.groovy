if (api.isInputGenerationExecution()) return

def minutesToWait = api.findLookupTableValues("BusinessRules", ["attribute2"], null, Filter.equal("name", "MinutesAfterPopulatingNewCRRows"))
        ?.find()?.attribute2?.toInteger() * -1

Calendar calendar = Calendar.getInstance()
calendar.add(Calendar.MINUTE, minutesToWait)

//def filter = Filter.lessOrEqual("lastUpdateDate", calendar.getTime())
api.local.pendingConditionRecords = getPendingConditionRecords(calendar.getTime())

def getPendingConditionRecordsOld(Filter addedFilters) {
    def filters = [
            Filter.equal("lookupTable.name", libs.QuoteConstantsLibrary.Tables.PENDING_CONDITION_RECORDS),
            Filter.equal("Status", libs.QuoteLibrary.Calculations.PENDING_STATUS)
    ]

    if (addedFilters) filters.add(addedFilters)

    return api.stream("JLTV", "lastUpdateDate", ["name", "Status", "Data", "Table"], *filters)?.withCloseable {
        it.collectEntries {
            [(it.name): [
                    Data : it.Data,
                    Table: it.Table
            ]]
        }
    }
}

def getPendingConditionRecords(time) {
    def qapi = api.queryApi()

    def t1 = qapi.tables().companyParameterRows(libs.QuoteConstantsLibrary.Tables.PENDING_CONDITION_RECORDS)

    def filter = qapi.exprs().and(
            t1.lastUpdateDate().lessOrEqual(time),
            t1.Status.equal(libs.QuoteLibrary.Calculations.PENDING_STATUS)
    )

    return qapi.source(t1, [t1.key1(), t1.Status, t1.Data, t1.Table], filter).stream { it.collectEntries {
        [(it.key1): [
                Data: it.Data,
                Table: it.Table
        ]] } } ?: [:]
}