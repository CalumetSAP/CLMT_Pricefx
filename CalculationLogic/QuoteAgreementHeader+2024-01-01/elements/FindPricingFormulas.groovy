if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def pricingFormulas = api.findLookupTableValues(tablesConstants.PRICING_FORMULA)?.attribute1

return pricingFormulas