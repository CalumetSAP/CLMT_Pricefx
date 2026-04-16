if (api.isInputGenerationExecution() || (!api.local.lineItemSkus && !api.local.addedContracts)) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
def key4
api.local.guardrailsTable = api.findLookupTableValues(tablesConstants.GUARDRAILS, null, null, null)?.collectEntries {
    key4 = it.key3 != "*" && it.key4 != "*" ? "*" : it.key4
    [(it.key1 + "|" + it.key2 + "|" + it.key3 + "|" + key4 + "|" + it.key5): it]
}

return null