if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

def costData = out.FindCostPX
if (!costData) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def productMasterData = out.FindProductMasterData ?: [:]
def globalUOMConversionMap = out.FindGlobalUOMConversionTable ?: [:]
def uomConversionMap = out.FindUOMConversionTable ?: [:]

def costMap = [:]
def materialMarginMap = [:]

def sku, dsData, costAttrs, productMasterItem, units, price, pricingUOM, cost, conversionFactor, margin, marginRounded
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    sku = lnProduct.sku
    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)

    costAttrs = costData?.get(sku + "|" + removePlantDescription(dsData?.get("Plant")))
    productMasterItem = productMasterData?.get(sku)

    units = calculations.getInputValue(lnProduct, lineItemConstants.PER_ID) ?: BigDecimal.ONE
    price = costAttrs?.StandardPrice?.replace(",", "")?.toBigDecimal() ? costAttrs?.StandardPrice?.replace(",", "")?.toBigDecimal() : costAttrs?.SecondayPrice?.replace(",", "")?.toBigDecimal()
    pricingUOM = calculations.getInputValue(lnProduct, lineItemConstants.PRICING_UOM_ID) ?: productMasterItem?.UOM

    cost = calculations.calculateLineItemCost(price, costAttrs?.CostingLotSize?.replace(",", "")?.toBigDecimal(), units?.toBigDecimal())

    conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(sku, productMasterItem?.UOM, pricingUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()

    cost = conversionFactor && cost ? cost * conversionFactor : cost

    if (!cost) continue

    costMap?.put(lnProduct.lineId, cost)

    if (!calculations.getInputValue(lnProduct, lineItemConstants.PRICE_ID)) continue

    margin = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_ID)?.toBigDecimal() - cost?.toBigDecimal()
    marginRounded = libs.QuoteLibrary.RoundingUtils.round(margin, 2)

    materialMarginMap?.put(lnProduct.lineId, marginRounded)
}

api.local.costMap = costMap
api.local.materialMarginMap = materialMarginMap

return null

def removePlantDescription(plant) {
    return plant?.split(" ")?.getAt(0)
}