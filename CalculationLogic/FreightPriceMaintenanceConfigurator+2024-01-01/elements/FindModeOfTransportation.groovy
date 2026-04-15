if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows(tablesConstants.MODE_OF_TRANSPORTATION)

return qapi.source(t1, [t1.key1(), t1.Description]).stream { it.collectEntries { [(it.key1): it.key1 + " - " + it.Description] } } ?: [:]