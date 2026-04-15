if (!quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def modeOfTransportations = api.local.modeOfTransportations?.findAll { it }?.unique()
if (!modeOfTransportations) return [:]

def filter = Filter.in("name", modeOfTransportations)

return api.findLookupTableValues(tablesConstants.MODE_OF_TRANSPORTATION_GROUP, null, null, filter)?.collectEntries {[
        (it.name): it.attribute1?.toUpperCase()
]} ?: [:]