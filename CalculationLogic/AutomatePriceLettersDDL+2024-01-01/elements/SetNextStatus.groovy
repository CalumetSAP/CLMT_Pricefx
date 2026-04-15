if (api.isInputGenerationExecution() || api.isDebugMode()) return

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("AffectedVariants")
def fields = [
        t1.key1(),
        t1.key2(),
        t1.key3(),
        t1.key4(),
        t1.UUID
]
def filter = qapi.exprs().and(
        qapi.exprs().or(
                t1.DataSourceLoaded.equal(false),
                t1.DataSourceLoaded.isNull()
        )
)

def processingRows = qapi.source(t1, fields, filter)
        .stream {
            it.collect { it }
        }

if (!processingRows) return

updateRowsStatus(processingRows)

return null

def updateRowsStatus(List rows) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.AFFECTED_VARIANTS)
    rows.each { r ->
        api.addOrUpdate("MLTV4", buildRowToAddOrUpdate(cptData, r))
    }
}

private def buildRowToAddOrUpdate(cptData, row) {
    def ppRow = [
            "lookupTableId"  : cptData.id,
            "lookupTableName": cptData.uniqueName,
            "key1"           : row.key1,
            "key2"           : row.key2,
            "key3"           : row.key3,
            "key4"           : row.key4,
            "attribute1"     : true,
            "attribute2"     : false,
            "attribute3"     : row.UUID
    ]

    return ppRow
}