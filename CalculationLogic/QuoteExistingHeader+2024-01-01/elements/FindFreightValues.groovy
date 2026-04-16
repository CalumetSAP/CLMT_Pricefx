if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def contracts = []
def lines = []
def materials = []
def pvfByKey = [:]
def configuratorValuesMap = [:]

def configurator, dsData, priceValidFrom, freightAmount, freightUOM
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    priceValidFrom = findValue(api.local.lineItemChanged == lnProduct.lineId, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configurator)
    freightAmount = configurator?.get(lineItemConstants.FREIGHT_AMOUNT_ID)
    freightUOM = configurator?.get(lineItemConstants.FREIGHT_UOM_ID)
    if (freightAmount && freightUOM) {
        configuratorValuesMap[lnProduct.lineId] = [
                FreightAmount: freightAmount,
                FreightUOM   : freightUOM
        ]
        continue
    }
    if (!priceValidFrom) continue

    contracts << dsData.SAPContractNumber
    lines     << dsData.SAPLineId
    materials << lnProduct.sku

    def key = "${dsData.SAPContractNumber}|${dsData.SAPLineId}|${lnProduct.sku}"
    pvfByKey.put(key.toString(), priceValidFrom)

//    if (priceValidFrom) {
//        freightFilters.add(Filter.and(
//                Filter.equal("SAPContractNumber", dsData.SAPContractNumber),
//                Filter.equal("SAPLineID", dsData.SAPLineId),
//                Filter.equal("Material", lnProduct.sku),
//                Filter.lessOrEqual("FreightValidFrom", priceValidFrom),
//                Filter.greaterOrEqual("FreightValidto", priceValidFrom)
//        ))
//    }
}
api.local.freightValuesFromConfigurator = configuratorValuesMap
if (!pvfByKey) return [:]

def allPvf = pvfByKey.values().flatten()
def minPvf = allPvf.min()
def maxPvf = allPvf.max()

Filter freightFilters = Filter.and(
        Filter.in("SAPContractNumber", contracts.unique().findAll{it!=null}),
        Filter.in("SAPLineID",       lines.unique().findAll{it!=null}),
        Filter.in("Material",        materials.unique().findAll{it!=null}),
        Filter.lessOrEqual("FreightValidFrom", maxPvf),
        Filter.greaterOrEqual("FreightValidto", minPvf)
)
return libs.QuoteLibrary.Query.findFreightValues(freightFilters, pvfByKey)

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}