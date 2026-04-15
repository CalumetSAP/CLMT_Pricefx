if (!quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables
final calculations = libs.QuoteLibrary.Calculations

//def modeOfTransportationsGroup = out.FindModeOfTransportationGroup?.values()?.toList()
//
//def filter = Filter.in("key2", modeOfTransportationsGroup)
//
//def data = api.findLookupTableValues(tablesConstants.GUARDRAIL, null, null, filter)
//
//return calculations.groupGuardrailData(data)

return api.findLookupTableValues(tablesConstants.GUARDRAILS, null, null, null)?.collectEntries {
    [(it.key1 + "|" + it.key2 + "|" + it.key3 + "|" + it.key4 + "|" + it.key5): it]
}