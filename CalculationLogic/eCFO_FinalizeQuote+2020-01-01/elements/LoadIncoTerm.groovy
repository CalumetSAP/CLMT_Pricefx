if (customFormProcessor.isPostPhase()) return

if (!api.global.incoTerms) {
    api.global.incoTerms = api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.INCO_TERM)?.collectEntries { [(it.name): it.name + " - " + it.attribute1] }
}

return api.global.incoTerms