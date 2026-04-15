if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]

def basePricingFilters = []
def basePricingCRFilters = []
def quotesPricingFilters = []
def indexValuesList = []

def useConfiguratorValue
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    useConfiguratorValue = api.local.lineItemChanged == lnProduct.lineId

    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    def dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)

    def pricelist = findValue(useConfiguratorValue, lineItemConstants.PRICE_LIST_ID, lnProduct, configurator)
    def priceTypeAux = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
    def priceType = useConfiguratorValue
            ? configurator?.get(lineItemConstants.PRICE_TYPE_ID)
            : priceTypeAux ? dropdownOptions["PriceType"]?.find { k, v -> v.toString().startsWith(priceTypeAux as String) }?.key : priceTypeAux
    if (pricelist) {
        pricelist = pricelist?.split(" - ")?.getAt(0)
        if (priceType == "3") {
            def commonFilter = Filter.and(
                    Filter.lessOrEqual("ValidFrom", findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)),
                    Filter.greaterOrEqual("ValidTo", findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)),
                    Filter.equal("Material", lnProduct.sku)
            )
            basePricingFilters.add(Filter.and(
                    commonFilter,
                    Filter.equal("Pricelist", pricelist)
            ))

            //CR filters
            commonFilter = Filter.and(
                    Filter.lessOrEqual("validFrom", findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)?.toString()),
                    Filter.greaterOrEqual("validTo", findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)?.toString()),
                    Filter.equal("key5", lnProduct.sku)
            )
            basePricingCRFilters.add(Filter.and(
                    commonFilter,
                    Filter.equal("key2", dsData.SalesOrg),
                    Filter.equal("key4", pricelist)
            ))
        }
    }
    if (priceType == "2") {
        quotesPricingFilters.add(Filter.and(
                Filter.lessOrEqual("PriceValidFrom", findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)),
                Filter.greaterOrEqual("PriceValidTo", findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)),
                Filter.equal("QuoteID", dsData.QuoteID),
                Filter.equal("LineID", dsData.LineId),
                Filter.equal("Material", lnProduct.sku),
//                    Filter.equal("PriceListPLT", pricelist)
        ))
    }
    if (findValue(useConfiguratorValue, lineItemConstants.PLANT_ID, lnProduct, configurator)) findValue(useConfiguratorValue, lineItemConstants.PLANT_ID, lnProduct, configurator)
    if (findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, lnProduct, configurator)) {
        indexValuesList.add(findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, lnProduct, configurator))
    }
}

api.local.basePricingFilters = basePricingFilters
api.local.basePricingCRFilters = basePricingCRFilters
api.local.quotesPricingFilters = quotesPricingFilters
api.local.indexValuesList = indexValuesList

return null

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}