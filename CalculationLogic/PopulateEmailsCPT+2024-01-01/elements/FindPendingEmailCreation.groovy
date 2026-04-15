if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("AffectedVariants")
def filter = qapi.exprs().and(
        t1.DataSourceLoaded.equal(true),
        qapi.exprs().or(
                t1.EmailsCreated.equal(false),
                t1.EmailsCreated.isNull()
        )
)

api.global.pendingEmails = qapi.source(t1, [t1.Dashboard, t1.UUID, t1.Variant, t1.EffectiveDate, t1.ChangeDate, t1.DataSourceLoaded], filter).stream {
    it.collect().groupBy { it.Dashboard }
} ?: [:]

return null