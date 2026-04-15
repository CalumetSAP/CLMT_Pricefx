if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def freightFilters = []

def useConfiguratorValue, configurator, dsData, priceValidFrom
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    useConfiguratorValue = api.local.lineItemChanged == lnProduct.lineId
    configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    priceValidFrom = findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)

    if (priceValidFrom) {
        freightFilters.add(Filter.and(
                Filter.equal("SAPContractNumber", dsData.SAPContractNumber),
                Filter.equal("SAPLineID", dsData.SAPLineId),
                Filter.equal("Material", lnProduct.sku),
                Filter.lessOrEqual("FreightValidFrom", priceValidFrom),
                Filter.greaterOrEqual("FreightValidto", priceValidFrom)
        ))
    }
}

return libs.QuoteLibrary.Query.findFreightValues(freightFilters)

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}