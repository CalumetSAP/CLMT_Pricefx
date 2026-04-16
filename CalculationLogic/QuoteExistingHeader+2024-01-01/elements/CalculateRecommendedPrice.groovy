if (api.isInputGenerationExecution() || !quoteProcessor.isPrePhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def soldToOnly = headerConfigurator?.get(headerConstants.SOLD_TO_ONLY_QUOTE_ID)
def soldToIndustry = out.FindIndustryList && !api.isInputGenerationExecution() ? out.FindIndustryList : null

def globalUOMConversionMap = out.FindGlobalUOMConversionTable ?: [:]
def uomConversionMap = out.FindUOMConversionTable ?: [:]
def guardrailMap = api.local.guardrailsTable ?: [:]
def packageDifferentialMap = out.FindPackageDifferential ?: [:]
def approversMap = out.FindApprovers ?: [:]
def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]

def customerMasterData = api.local.shipToOutputData ?: [:]

def data = [:]
def sku, industry, plant, price, priceType, pricingUOM, phs, product, material, pricelist, basePricing, basePricingUOM, pricingMapAux,
        moq, moqUOM, guardrailValues, key, numberOfDecimals, dsData, filteredApproversMap, scaleUOM, salesPerson, pricingMap, priceValidFrom

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    sku = lnProduct.sku

    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    filteredApproversMap = approversMap?.get(dsData.Division ?: "")?.get(dsData.SalesOrg ?: "")

    if (api.local.configuratorHasChanged && api.local.lineItemChanged == lnProduct.lineId) {
        def configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
        priceType = configurator?.get(lineItemConstants.PRICE_TYPE_ID)
        if (!sku || !priceType || priceType == "4") continue

        industry = soldToOnly ? soldToIndustry : configurator?.get(lineItemConstants.SHIP_TO_INDUSTRY_ID)
        plant = calculations.removePlantDescription([configurator?.get(lineItemConstants.PLANT_ID)])?.find()
        price = configurator?.get(lineItemConstants.PRICE_ID)
        pricelist = configurator?.get(lineItemConstants.PRICE_LIST_ID)?.split(" - ")?.getAt(0)
        pricingUOM = configurator?.get(lineItemConstants.PRICING_UOM_ID)
        moq = configurator?.get(lineItemConstants.MOQ_ID)
        moqUOM = configurator?.get(lineItemConstants.MOQ_UOM_ID)

        material = sku.size() > 6 ? sku.take(6) : sku
        numberOfDecimals = configurator?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: "2"
        salesPerson = configurator?.get(lineItemConstants.SALES_PERSON_ID)
        priceValidFrom = configurator?.get(lineItemConstants.PRICE_VALID_FROM_ID)
    } else {
        def priceTypeAux = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
        priceType = priceTypeAux ? dropdownOptions["PriceType"]?.find { k, v -> v.toString().startsWith(priceTypeAux) }?.key : priceTypeAux
        if (!sku || !priceType || priceType == "4") continue

        def shipTo = dsData?.get("ShipTo")
        def customerMasterItem = customerMasterData?.get(shipTo)

        industry = soldToOnly ? soldToIndustry : customerMasterItem?.get("Industry")
        plant = calculations.removePlantDescription([dsData?.get("Plant")])?.find()
        price = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_ID)
        pricelist = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_LIST_ID)?.split(" - ")?.getAt(0)
        pricingUOM = calculations.getInputValue(lnProduct, lineItemConstants.PRICING_UOM_ID)
        moq = calculations.getInputValue(lnProduct, lineItemConstants.MOQ_ID)
        moqUOM = calculations.getInputValue(lnProduct, lineItemConstants.MOQ_UOM_ID)

        material = sku.size() > 6 ? sku.take(6) : sku
        numberOfDecimals = calculations.getInputValue(lnProduct, lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: "2"
        salesPerson = calculations.getInputValue(lnProduct, lineItemConstants.SALES_PERSON_ID)
        priceValidFrom = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_VALID_FROM_ID)
    }

    salesPerson = salesPerson?.contains(" - ") ? salesPerson?.split(" - ")[0] : salesPerson

    product = out.FindProductMasterData?.get(sku)
    phs = []
    if (product?.PH4Code) phs.add(product?.PH4Code)
    if (product?.PH3Code) phs.add(product?.PH3Code)
    if (product?.PH2Code) phs.add(product?.PH2Code)
    if (product?.PH1Code) phs.add(product?.PH1Code)

//    key = null
//    basePricing = null
//    basePricingUOM = null
//    if (priceType == "2") {
//        key = dsData.QuoteID + "|" + dsData.LineId + "|" + pricelist
//        scaleUOM = null
//        if (api.local.basePricingMap?.QuoteID && api.local.basePricingMap?.LineID) {
//            scaleUOM = (out.FindQuoteScales?.get(api.local.basePricingMap?.QuoteID + "|" + api.local.basePricingMap?.LineID) as List)?.find().ScaleUOM
//        }
//        basePricing = calculations.findBasePricingNew(key, moq, moqUOM, api.local.basePricingMap, out.FindQuoteScales, scaleUOM, sku, uomConversionMap, globalUOMConversionMap)
//        basePricingUOM = api.local.basePricingMap?.get(key)?.UOM
//    } else if (priceType == "3") {
    key = (dsData.SalesOrg ?: "") + "|" + pricelist + "|" + lnProduct?.sku
    pricingMapAux = out.ZBPLMerged?.get(key)?.max { it.ValidFrom }
    pricingMap = [:]
    if (pricingMapAux) pricingMap.put(key, pricingMapAux)
    basePricing = calculations.findBasePricingNew(key, moq, moqUOM, pricingMap, out.FindZBPLScales, pricingMapAux?.ScaleUOM, sku, uomConversionMap, globalUOMConversionMap)
    basePricingUOM = pricingMap?.get(key)?.UOM
//    }

    guardrailValues = calculations.calculateGuardrailsValues(guardrailMap, industry, plant, material, phs, pricelist, price, globalUOMConversionMap, uomConversionMap, pricingUOM, sku,
            priceType, basePricing, basePricingUOM, packageDifferentialMap, filteredApproversMap, product, numberOfDecimals, salesPerson)

    data.put(lnProduct.lineId, guardrailValues)
}

return data