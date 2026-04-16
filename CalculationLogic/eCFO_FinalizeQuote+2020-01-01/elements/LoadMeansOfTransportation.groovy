if (customFormProcessor.isPostPhase()) return

if (!api.global.meansOfTransportation) {
    api.global.meansOfTransportation = api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.MEANS_OF_TRANSPORTATION)?.collectEntries { [(it.name): it.name + " - " + it.attribute1] }
}

return api.global.meansOfTransportation