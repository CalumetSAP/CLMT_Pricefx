if (!api.global.plantsDescriptions) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("Plant")

    api.global.plantsDescriptions = qapi.source(t1, [t1.key1(), t1."Attribute 1"]).stream { it.collectEntries {
        [(it.key1): it."Attribute 1"]
    } }
}

return api.global.plantsDescriptions?.get(api.local.plantCode)