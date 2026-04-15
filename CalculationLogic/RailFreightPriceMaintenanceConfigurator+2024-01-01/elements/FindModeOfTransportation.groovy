if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows("FreightBusinessRules")
List<String> railModesOfTransportation = qapi.source(t1, [t1.Code], t1.Field.equal("RailModeOfTransportation"))
        .stream { it.collect().Code } ?: []

def defaultRailModeOfTransportation = null
if (railModesOfTransportation.size() == 1) {
    defaultRailModeOfTransportation = railModesOfTransportation.find()
}
api.local.defaultRailModeOfTransportation = defaultRailModeOfTransportation

def t2 = qapi.tables().companyParameterRows(libs.QuoteConstantsLibrary.Tables.MODE_OF_TRANSPORTATION)

return qapi.source(t2, [t2.key1(), t2.Description], t2.key1().in(railModesOfTransportation)).stream { it.collectEntries { [(it.key1): it.key1 + " - " + it.Description] } } ?: [:]