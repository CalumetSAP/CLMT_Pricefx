if (api.isInputGenerationExecution() || !InputPlant?.input?.getValue()) return

def costData = api.local.costPX
if (!costData) return

final calculations = libs.QuoteLibrary.Calculations

def globalUOMConversionMap = api.local.globalUOMTable ?: [:]
def uomConversionMap = api.local.uomTable ?: [:]

def sku = InputMaterial?.input?.getValue()

def costAttrs = costData?.get(sku + "|" + removePlantDescription(InputPlant?.input?.getValue()))
def units = InputPer?.input?.getValue() ?: BigDecimal.ONE

def price = costAttrs?.StandardPrice?.replace(",", "")?.toBigDecimal() ? costAttrs?.StandardPrice?.replace(",", "")?.toBigDecimal() : costAttrs?.SecondayPrice?.replace(",", "")?.toBigDecimal()

def cost = calculations.calculateLineItemCost(price, costAttrs?.CostingLotSize?.replace(",", "")?.toBigDecimal(), units?.toBigDecimal())

def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(sku, api.local.product?.UOM, InputPricingUOM?.input?.getValue(), uomConversionMap, globalUOMConversionMap)?.toBigDecimal()

cost = conversionFactor ? cost * conversionFactor : cost

return cost

def removePlantDescription(plant) {
    return plant?.split(" ")?.getAt(0)
}