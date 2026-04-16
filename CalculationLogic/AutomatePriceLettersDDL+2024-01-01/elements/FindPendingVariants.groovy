if (api.isInputGenerationExecution() || api.isDebugMode()) return

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows("AffectedVariants")
def fields = [
        t1.key3().as("Dashboard"),
        t1.DataSourceLoaded
]
def filter = qapi.exprs().and(
        qapi.exprs().or(
                t1.DataSourceLoaded.equal(false),
                t1.DataSourceLoaded.isNull()
        )
)

def rows = qapi.source(t1, fields, filter)
        .distinct()
        .stream {
            it.collect { it.Dashboard }
        }

rows.eachWithIndex { r, i ->
    dist.addOrUpdateCalcItem("batchNo-$i", i, [dashboard: r])
}