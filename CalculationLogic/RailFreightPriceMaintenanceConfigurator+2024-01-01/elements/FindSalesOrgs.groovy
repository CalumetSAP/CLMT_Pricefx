if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows(tablesConstants.SALES_ORG)

return qapi.source(t1, [t1.key1(), t1.SODescription], t1."Show on List Price Maintenance".equal(true)).stream {
    it.collectEntries { [(it.key1): it.key1 + " - " + it.SODescription] }
} ?: [:]