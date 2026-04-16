if (api.isInputGenerationExecution() || !quoteProcessor.isPrePhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def salesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)
def soldToOnly = headerConfigurator?.get(headerConstants.SOLD_TO_ONLY_QUOTE_ID)
def soldToIndustry = out.FindIndustryList && !api.isInputGenerationExecution() ? out.FindIndustryList : null

def globalUOMConversionMap = out.FindGlobalUOMConversionTable ?: [:]
def uomConversionMap = out.FindUOMConversionTable ?: [:]
def guardrailMap = api.local.guardrailsTable ?: [:]
def packageDifferentialMap = out.FindPackageDifferential ?: [:]
def approversMap = out.FindApprovers ?: [:]

def data = [:]
def sku, industry, plant, price, priceType, pricingUOM, phs, product, material, pricelist, basePricing, basePricingUOM, moq, guardrailValues, numberOfDecimals, key, salesPerson,
        moqUOM, priceValidFrom, pricingMap, pricingMapAux

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)
    sku = configurator?.get(lineItemConstants.MATERIAL_ID)
    priceType = configurator?.get(lineItemConstants.PRICE_TYPE_ID)
    if (!sku || !priceType || priceType == "4") continue

    industry = soldToOnly ? soldToIndustry : configurator?.get(lineItemConstants.SHIP_TO_INDUSTRY_ID)
    plant = calculations.removePlantDescription([configurator?.get(lineItemConstants.PLANT_ID)])?.find()
    price = configurator?.get(lineItemConstants.PRICE_ID)
    pricelist = configurator?.get(lineItemConstants.PRICE_LIST_ID)?.split(" - ")?.getAt(0)
    pricingUOM = configurator?.get(lineItemConstants.PRICING_UOM_ID)
    priceValidFrom = configurator?.get(lineItemConstants.PRICE_VALID_FROM_ID)

    product = out.FindProductMasterData?.get(lnProduct?.sku)
    phs = []
    if (product?.PH4Code) phs.add(product?.PH4Code)
    if (product?.PH3Code) phs.add(product?.PH3Code)
    if (product?.PH2Code) phs.add(product?.PH2Code)
    if (product?.PH1Code) phs.add(product?.PH1Code)

    material = sku.size() > 6 ? sku.take(6) : sku
    moq = configurator?.get(lineItemConstants.MOQ_ID)
    moqUOM = configurator?.get(lineItemConstants.MOQ_UOM_ID)
    key = salesOrg + "|" + pricelist + "|" + lnProduct?.sku
    pricingMapAux = out.ZBPLMerged?.get(key)?.max { it.ValidFrom }
    pricingMap = [:]
    if (pricingMapAux) pricingMap.put(key, pricingMapAux)
    basePricing = calculations.findBasePricingNew(key, moq, moqUOM, pricingMap, out.FindZBPLScales, pricingMapAux?.ScaleUOM, sku, uomConversionMap, globalUOMConversionMap)
    basePricingUOM = pricingMap?.get(key)?.UOM
    numberOfDecimals = configurator?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: "2"
    salesPerson = configurator?.get(lineItemConstants.SALES_PERSON_ID)
    salesPerson = salesPerson?.contains(" - ") ? salesPerson?.split(" - ")[0] : salesPerson

    guardrailValues = calculations.calculateGuardrailsValues(guardrailMap, industry, plant, material, phs, pricelist, price, globalUOMConversionMap, uomConversionMap, pricingUOM, sku,
            priceType, basePricing, basePricingUOM, packageDifferentialMap, approversMap, product, numberOfDecimals, salesPerson)

    data.put(lnProduct.lineId, guardrailValues)
}

return data