if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

if (!api.local.addedContracts) return

final calculations = libs.QuoteLibrary.Calculations

def globalUOMConversionMap = out.FindGlobalUOMConversionTable ?: [:]
def uomConversionMap = out.FindUOMConversionTable ?: [:]
def guardrailMap = api.local.guardrailsTable ?: [:]
def packageDifferentialMap = out.FindPackageDifferential ?: [:]
def approversMap = out.FindApprovers ?: [:]

def data = [:]
def sku, industry, plant, price, priceType, pricingUOM, phs, product, material, pricelist, basePricing, basePricingUOM, pricingMapAux,
    moq, moqUOM, guardrailValues, key, numberOfDecimals, filteredApproversMap, salesPerson, pricingMap, priceValidFrom

def contracts = out.FindContractDSData
contracts?.each { contractNumber, lines ->
    lines?.each { line ->
        sku = line?.Material

        filteredApproversMap = approversMap?.get(line?.Division ?: "")?.get(line?.SalesOrg ?: "")

        priceType = line?.PriceType
        if (!sku || !priceType || priceType == "4") return

        industry = api.local.shipToOutputData?.get(line?.ShipTo)?.get("Industry")
        plant = calculations.removePlantDescription([line?.Plant])?.find()
        price = line?.Price
        pricelist = line?.PriceListPLT
        pricingUOM = line?.PricingUOM
        moq = line?.MOQ
        moqUOM = line?.MOQUOM

        material = sku.size() > 6 ? sku.take(6) : sku
        numberOfDecimals = line?.NumberofDecimals ?: "2"
        salesPerson = line?.SalesPerson
        priceValidFrom = line?.PriceValidFrom

        salesPerson = salesPerson?.contains(" - ") ? salesPerson?.split(" - ")[0] : salesPerson

        product = out.FindProductMasterData?.get(sku)
        phs = []
        if (product?.PH4Code) phs.add(product?.PH4Code)
        if (product?.PH3Code) phs.add(product?.PH3Code)
        if (product?.PH2Code) phs.add(product?.PH2Code)
        if (product?.PH1Code) phs.add(product?.PH1Code)

        key = (line?.SalesOrg ?: "") + "|" + pricelist + "|" + sku
        pricingMapAux = out.InitZBPLMerged?.get(key)?.max { it.ValidFrom }
        pricingMap = [:]
        if (pricingMapAux) pricingMap.put(key, pricingMapAux)
        basePricing = calculations.findBasePricingNew(key, moq, moqUOM, pricingMap, out.FindInitZBPLScales, pricingMapAux?.ScaleUOM, sku, uomConversionMap, globalUOMConversionMap)
        basePricingUOM = pricingMap?.get(key)?.UOM

        guardrailValues = calculations.calculateGuardrailsValues(guardrailMap, industry, plant, material, phs, pricelist, price, globalUOMConversionMap, uomConversionMap, pricingUOM, sku,
                priceType, basePricing, basePricingUOM, packageDifferentialMap, filteredApproversMap, product, numberOfDecimals, salesPerson)

        data.put(line?.SAPContract + "|" + line?.LineNumber, guardrailValues)
    }
}

return data