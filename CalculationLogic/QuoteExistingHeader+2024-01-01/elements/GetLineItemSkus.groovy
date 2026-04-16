if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def lineItemSkus = []
def lineItemShipTos = []
def lineItemPriceType = new HashSet<String>()
def lineItemPricingFilters = []


for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder || api.local.removedLineIds?.contains(lnProduct.lineId)) continue

    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    def dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)

    def useConfiguratorValue = configurator?.get(lineItemConstants.CONFIGURATOR_HAS_CHANGED_ID) == true

    lineItemSkus.add(lnProduct.sku)
    lineItemShipTos.add(dsData.ShipTo)
    lineItemPriceType.add(calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID) as String)

    def priceValidFrom = findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)
    if (priceValidFrom) {
        lineItemPricingFilters.add(Filter.and(
                Filter.lessOrEqual("ValidFrom", priceValidFrom),
                Filter.greaterOrEqual("ValidTo", priceValidFrom),
                Filter.equal("Material", lnProduct.sku),
                Filter.isNotNull("Amount")
        ))
    }
}

if (api.local.addedContracts) {
    def contracts = out.FindContractDSData

    contracts?.each { contractNumber, lines ->
        lines?.each { line ->
            lineItemSkus.add(line.Material)
            lineItemShipTos.add(line.ShipTo)
            lineItemPriceType.add(line.PriceType)
            if (line.PriceValidFrom && line.PriceValidTo && line.Material) {
                lineItemPricingFilters.add(Filter.and(
//                        Filter.lessOrEqual("ValidFrom", line.PriceValidFrom),
//                        Filter.greaterOrEqual("ValidTo", line.PriceValidTo),
                        Filter.equal("Material", line.Material),
                        Filter.isNotNull("Amount")
                ))
            }
        }
    }
}

api.local.lineItemSkus = lineItemSkus
api.local.lineItemShipTos = lineItemShipTos
api.local.lineItemPriceType = lineItemPriceType
api.local.lineItemPricingFilters = lineItemPricingFilters

return null

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}