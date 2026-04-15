def findPendingAffectedVariantsByDashboard(String dashboard) {
    def qapi = api.queryApi()

    def t1 = qapi.tables().companyParameterRows("AffectedVariants")
    def fields = [
            t1.key1().as("Variant"),
            t1.key2().as("EffectiveDate"),
            t1.key4().as("ChangeDate"),
            t1.UUID,
            t1.DataSourceLoaded
    ]
    def filter = qapi.exprs().and(
            qapi.exprs().or(
                    t1.DataSourceLoaded.equal(false),
                    t1.DataSourceLoaded.isNull()
            ),
            t1.Dashboard.equal(dashboard)
    )

    return qapi.source(t1, fields, filter).stream {
        it.collect { it }
    }
}

def findSPSVariantsByNames(List variants) {
    if (!variants) return []

    def qapi = api.queryApi()

    def t1 = qapi.tables().companyParameterRows("VariantsPricePublishingDashboard")

    return qapi.source(t1, t1.key1().in(variants)).stream { rows ->
        rows.collect { row ->
            row.collectEntries { k, v ->
                [(k): decodeIfJson(v)]
            }
        }
    }
}

def findPBVariantsByNames(List variants) {
    if (!variants) return []

    def qapi = api.queryApi()

    def t1 = qapi.tables().companyParameterRows("VariantsPBPricePublishingDashboard")

    return qapi.source(t1, t1.key1().in(variants)).stream { rows ->
        rows.collect { row ->
            row.collectEntries { k, v ->
                [(k): decodeIfJson(v)]
            }
        }
    }
}

private def decodeIfJson(v) {
    if (!(v instanceof String)) return v
    def s = v.trim()
    try {
        if (s.startsWith("[") && s.endsWith("]")) {
            return api.jsonDecodeList(s)
        }
        if (s.startsWith("{") && s.endsWith("}")) {
            return api.jsonDecode(s)
        }
    } catch (ignored) {}
    return v
}