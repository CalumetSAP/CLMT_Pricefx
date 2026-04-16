if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows(tablesConstants.DIVISION)

return qapi.source(t1, [t1.key1(), t1.DivisionName]).stream { it.collectEntries { [(it.key1): it.key1 + " - " + it.DivisionName] } } ?: [:]