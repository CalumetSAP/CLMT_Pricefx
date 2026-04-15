if (!quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def costData = out.FindCostPX
if (!costData) return

def uomConversionMap = out.FindUOMConversionTable
def lineItemCalculations = [:]
def costAttrs, units, cost, uomConversionValues
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String

    costAttrs = costData?.get(lnProduct.sku + "|" + removePlantDescription(calculations.getInputValue(lnProduct, lineItemConstants.PLANT_ID)))
    units = calculations.getInputValue(lnProduct, lineItemConstants.PER_ID)

    cost = calculations.calculateLineItemCost(costAttrs?.StandardPrice?.replace(",", "")?.toBigDecimal(), costAttrs?.CostingLotSize?.replace(",", "")?.toBigDecimal(), units?.toBigDecimal())
    uomConversionValues = uomConversionMap?.get(lnProduct.sku + "|" + calculations.getInputValue(lnProduct, lineItemConstants.PRICING_UOM_ID))

    lineItemCalculations.put(lineId, [
            Cost: calculations.convertValue(cost, uomConversionValues?.Numerator, uomConversionValues?.Denominator)
    ])
}

return lineItemCalculations

def removePlantDescription(plant) {
    return plant?.split(" ")?.getAt(0)
}