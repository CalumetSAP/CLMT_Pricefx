if (api.isInputGenerationExecution() || (!api.local.lineItemSkus && !api.local.addedContracts)) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

final calculations = libs.QuoteLibrary.Calculations

def fields = ["key1", "key2", "key3", "key4", "key5", "key6", "attribute3"]

def data = api.findLookupTableValues(tablesConstants.APPROVERS_TABLE, fields, null, null)

return calculations.groupTotalApproversData(data)