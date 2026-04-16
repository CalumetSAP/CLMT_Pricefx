if (api.isInputGenerationExecution() || !InputPriceType?.input?.getValue()) return

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
if (!havePermissions) return

final calculations = libs.QuoteLibrary.Calculations

def globalUOMConversionMap = api.local.globalUOMTable ?: [:]
def uomConversionMap = api.local.uomTable ?: [:]
def guardrailMap = api.local.guardrails ?: [:]
def packageDifferentialMap = api.local.packageDifferential ?: [:]
def approversMap = api.local.approversMap ?: [:]

def sku = InputMaterial?.input?.getValue()
def industry = api.local.isSoldToOnly ? api.local.soldToIndustry : InputShipToIndustry?.input?.getValue()
def plant = calculations.removePlantDescription([InputPlant?.input?.getValue()])?.find()
def price = InputPrice?.entry?.getFirstInput()?.getValue()
def pricelist = InputPricelist?.input?.getValue()?.split(" - ")?.getAt(0)
def priceType = InputPriceType?.input?.getValue()
def pricingUOM = InputPricingUOM?.input?.getValue()

if (priceType == "4") return null

def phs = []
if (api.local.product?.PH4Code) phs.add(api.local.product?.PH4Code)
if (api.local.product?.PH3Code) phs.add(api.local.product?.PH3Code)
if (api.local.product?.PH2Code) phs.add(api.local.product?.PH2Code)
if (api.local.product?.PH1Code) phs.add(api.local.product?.PH1Code)

def material = sku?.size() > 6 ? sku.take(6) : sku
def moq = InputMOQ?.input?.getValue()
def moqUOM = InputMOQUOM?.input?.getValue()
def scalesData = calculations.getScalesData(out.ZBPLMerged, out.FindZBPLScales)
def basePricing = calculations.findBasePricingLineItem(moq, moqUOM, out.ZBPLMerged, scalesData, out.ZBPLMerged?.ScaleUOM, sku, uomConversionMap, globalUOMConversionMap)
def basePricingUOM = out.ZBPLMerged?.UOM
def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"
def salesPerson = InputSalesPerson?.input?.getValue()
salesPerson = salesPerson?.contains(" - ") ? salesPerson?.split(" - ")[0] : salesPerson

def guardrailValues = calculations.calculateGuardrailsValues(guardrailMap, industry, plant, material, phs, pricelist, price, globalUOMConversionMap, uomConversionMap, pricingUOM, sku,
        priceType, basePricing, basePricingUOM, packageDifferentialMap, approversMap, api.local.product, numberOfDecimals, salesPerson)

return guardrailValues?.RecommendedPrice