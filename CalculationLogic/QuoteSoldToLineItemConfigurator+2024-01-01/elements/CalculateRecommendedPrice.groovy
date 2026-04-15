if (api.isInputGenerationExecution() || !InputPriceType?.input?.getValue()) return

final calculations = libs.QuoteLibrary.Calculations

def globalUOMConversionMap = api.local.globalUOMTable ?: [:]
def uomConversionMap = api.local.uomTable ?: [:]
def guardrailMap = api.local.guardrails ?: [:]
def packageDifferentialMap = api.local.packageDifferential ?: [:]
def approversMap = api.local.approversMap ?: [:]

def sku = InputMaterial?.input?.getValue()
def industry = api.local.soldToIndustry
def plant = calculations.removePlantDescription([InputPlant?.input?.getValue()])?.find()
def price = InputPrice?.entry?.getFirstInput()?.getValue()
def pricelist = InputPricelist?.input?.getValue()?.split(" - ")?.getAt(0)
def priceType = InputPriceType?.input?.getValue()
def pricingUOM = InputPricingUOM?.input?.getValue()

def phs = []
if (api.local.product?.PH4Code) phs.add(api.local.product?.PH4Code)
if (api.local.product?.PH3Code) phs.add(api.local.product?.PH3Code)
if (api.local.product?.PH2Code) phs.add(api.local.product?.PH2Code)
if (api.local.product?.PH1Code) phs.add(api.local.product?.PH1Code)

def material = sku?.size() > 6 ? sku.take(6) : sku
def moq = InputMOQ?.input?.getValue()
def moqUOM = InputMOQUOM?.input?.getValue()
def scalesData = calculations.getScalesData(out.FindQuotesDS, out.FindQuoteScales)
def basePricing = calculations.findBasePricingLineItem(moq, moqUOM, out.FindQuotesDS, scalesData, scalesData?.find()?.ScaleUOM, sku, uomConversionMap, globalUOMConversionMap)
def basePricingUOM = out.FindQuotesDS?.UOM

def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"

def guardrailValues = calculations.calculateGuardrailsValues(guardrailMap, industry, plant, material, phs, pricelist, price, globalUOMConversionMap, uomConversionMap, pricingUOM, sku,
        priceType, basePricing, basePricingUOM, packageDifferentialMap, approversMap, api.local.product, numberOfDecimals)

return guardrailValues?.RecommendedPrice