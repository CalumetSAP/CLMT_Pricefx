import java.text.SimpleDateFormat
import java.time.LocalDate

if (api.isInputGenerationExecution()) return

final dashboardName = "PB"

def calcItem = dist.calcItem
def dashboard = api.isDebugMode() ? dashboardName : calcItem?.Value?.dashboard

if (dashboard != dashboardName) return

final dashboardQuery = libs.DashboardLibrary.Query

def outputFormat = new SimpleDateFormat("MM/dd/yyyy")

def loader = api.isDebugMode() ? [] : dist.dataLoader

def visited = new HashSet<>()

def plants = api.global.plant
def customerMaster = api.global.customers
def products = api.global.products
def currencyDecimals = api.global.currencyDecimals
def uomDescription = api.global.uomDescription
def pricingData = api.global.pricingData

def quotes = out.LoadQuotes

List affectedVariants = dashboardQuery.findPendingAffectedVariantsByDashboard(dashboardName) ?: []
List variantsData = dashboardQuery.findPBVariantsByNames(affectedVariants.Variant) ?: []
Map variantsDataMap = variantsData.collectEntries {
    [(it.key1): it]
} ?: [:]

Calendar cal = Calendar.getInstance()

LocalDate effectiveDateLD
def variant, filteredLines, result, quotesValue, showJobbers, additionalNotes, masterParent, label, customerForPDF,
        pricingDateOutput
def pricingDate, effectiveDate, contracts, contractLines, materials, division, salesOrgs, soldTos, shipTos, pricelists, plant, salesPerson, salesPersonVariant
def exportRow, finalRow, material, legacyPart, customerMaterialNumber, materialDescription, price, priceUOM,
    origin, deliveredLocation, incotermsPerFreightPDF, pricingItem, jobbers, srp, map,
    productPM, shipToCustomer, shipTo, shipToList, maxDate, salesOrg, pricelist, scales, firstScale, validScales, scaleToMOQConversionFactor,
    moq, moqUOM, scaleQty, scalePrice, quoteCurrencyDecimals, scalePriceUOM, per, moqUomPdfExport, moqPerUOMPDF, soldToName,
    brand, productHierarchy1, productHierarchy2, productHierarchy3, priceUomPdfExport, quoteFreightPrice
def zbplItem, zbplCRItem, zbplItems, zbplCRItems, possibleItems, possibleCRItems
def keys, firstQuote
for (affectedVariant in affectedVariants) {
    variant = variantsDataMap[affectedVariant.Variant]
    if (!variant) continue

    effectiveDateLD = affectedVariant.EffectiveDate as LocalDate
    cal.clear()
    cal.set(effectiveDateLD.year, effectiveDateLD.monthValue - 1, effectiveDateLD.dayOfMonth)
    pricingDate = cal.getTime()

    filteredLines = quotes

    contracts = variant.Contract
    contractLines = variant.ContractLine
    materials = libs.DashboardLibrary.Calculations.findPossibleMaterialsForVariants(variant)
    soldTos = variant.SoldTo
    shipTos = variant.ShipTo
    division = variant.Division
    salesOrgs = variant.SalesOrg
    pricelists = variant.Pricelist
    plant = variant.Plant
    salesPersonVariant = variant.SalesPerson
    showJobbers = true // TODO: see if need to get from CPT

    if (!contracts && !contractLines && !materials && !division && !shipTos && !soldTos && !salesOrgs && !pricelists && !plant && !salesPersonVariant) continue

    filteredLines = filteredLines.findAll { it.PriceType != "4" }
    if (contracts) filteredLines = filteredLines.findAll { it.SAPContractNumber in contracts }
    if (contractLines) filteredLines = filteredLines.findAll { it.SAPLineID in contractLines }
    if (materials) filteredLines = filteredLines.findAll { it.Material in materials }
    if (soldTos) filteredLines = filteredLines.findAll { it.SoldTo in soldTos }
    if (shipTos) filteredLines = filteredLines.findAll { it.ShipTo in shipTos }
    if (division) filteredLines = filteredLines.findAll { it.Division == division }
    if (salesOrgs) filteredLines = filteredLines.findAll { it.SalesOrg in salesOrgs }
    if (pricelists) filteredLines = filteredLines.findAll { it.PriceListPLT in pricelists }
    if (plant) filteredLines = filteredLines.findAll { it.Plant in plant }
    if (salesPersonVariant) filteredLines = filteredLines.findAll { it.SalesPerson in salesPersonVariant }

    filteredLines = filteredLines.findAll { row -> row.PriceValidFrom && row.PriceValidTo }
    filteredLines = filteredLines.findAll { row ->
        row.PriceValidFrom <= pricingDate && row.PriceValidTo >= pricingDate
    }

    result = filteredLines.groupBy {
        if (it.PriceType == "3") {
            [ it.Material, it.CustomerMaterial, it.FreightTerm, it.Incoterm, normalizeBigDecimal(it.MOQ), it.MOQUOM ]
        } else {
            [ it.Material, it.CustomerMaterial, it.DeliveredPrice,
              it.PriceType, it.FreightTerm, it.Incoterm,
              normalizeBigDecimal(it.MOQ), it.MOQUOM ]
        }
    }

    additionalNotes = api.global.footers?.get(affectedVariant.Variant + "|30")?.value

    label = variant.Label
    masterParent = variant.MasterParent
    customerForPDF = null
    if(label){
        customerForPDF = label
    } else if (masterParent){
        customerForPDF = api.find("C", 0, 1, "customerId", ["name"], Filter.equal("customerId", masterParent))?.find()?.name
    }

    pricingDateOutput = outputFormat.format(pricingDate)

    for (value in result) {
        exportRow = [:]
        keys = value.getKey()

        (exportRow["Variant"] = affectedVariant.Variant)
        (exportRow["EffectiveDateKey"] = affectedVariant.EffectiveDate)
        (exportRow["ChangeDate"] = affectedVariant.ChangeDate)
        (exportRow["Dashboard"] = dashboardName)
        (exportRow["UUID"] = affectedVariant.UUID)
        (exportRow["SPSKey"] = "*")
        (exportRow["PBKey"] = keys.join("|"))
        (exportRow["additionalNotes"] = additionalNotes ? api.jsonEncode(additionalNotes) : null)
        (exportRow["customer"] = customerForPDF)
        (exportRow["pricingDate"] = pricingDateOutput)

        quotesValue = value.getValue()
        firstQuote = quotesValue?.find()

        shipToList = quotesValue?.collect { it.ShipTo }?.unique()
        deliveredLocation = null
        shipToCustomer = null
        if (shipToList?.size() == 1) {
            shipTo = shipToList.find()
            shipToCustomer = customerMaster.find { customer -> customer.customerId == shipTo }
            if (shipToCustomer?.name?.toLowerCase()?.contains("generic")) {
                deliveredLocation = "MULTIPLE"
            } else {
                deliveredLocation = [shipToCustomer?.attribute5, shipToCustomer?.attribute7]?.findAll{it != null}?.join(", ")
            }
        } else if (shipToList?.size() > 1) {
            deliveredLocation = "MULTIPLE"
        }
        salesPerson = customerMaster.find{customer -> customer.customerId == firstQuote?.SalesPerson}

        material = keys?.get(0)
        productPM = products?.find{product -> product.sku == material}

        materialDescription = productPM?.label
        legacyPart = productPM?.attribute12?.toString()
        brand = productPM?.attribute2
        productHierarchy1 = productPM?.attribute14
        productHierarchy2 = productPM?.attribute16
        productHierarchy3 = productPM?.attribute18

        customerMaterialNumber = [firstQuote?.CustomerMaterial, firstQuote?.ThirdPartyCustomer]?.findAll { it != null }?.join(" / ")
        soldToName = customerMaster.find { customer -> customer.customerId == firstQuote.SoldTo}?.name
        per = firstQuote?.Per
        price = firstQuote?.DeliveredPrice
        origin = plants?.get(firstQuote.Plant)
        incotermsPerFreightPDF = firstQuote?.Incoterm + " / \n" + firstQuote?.FreightTermValue
        moq = firstQuote?.MOQ
        moqUOM = firstQuote?.MOQUOM

        salesOrg = firstQuote?.SalesOrg
        pricelist = firstQuote?.PriceListPLT
        quoteCurrencyDecimals = currencyDecimals?.get(firstQuote?.Currency)

        pricingItem = pricingData?.get(material + "|" + pricelist)?.findAll {
            it.EffectiveDate <= pricingDate && it.ExpirationDate >= pricingDate
        }?.sort { !it.EffectiveDate }?.find()

        jobbers = libs.QuoteLibrary.RoundingUtils.round(pricingItem?.JobberPrice?.toBigDecimal(), (quoteCurrencyDecimals ? quoteCurrencyDecimals as Integer : 2))
        srp = libs.QuoteLibrary.RoundingUtils.round(pricingItem?.SRP?.toBigDecimal(), (quoteCurrencyDecimals ? quoteCurrencyDecimals as Integer : 2))
        map = libs.QuoteLibrary.RoundingUtils.round(pricingItem?.MAP?.toBigDecimal(), (quoteCurrencyDecimals ? quoteCurrencyDecimals as Integer : 2))

        quoteFreightPrice = firstQuote?.DeliveredPrice != null && firstQuote?.Price != null ? firstQuote?.DeliveredPrice - firstQuote?.Price : BigDecimal.ZERO

        (exportRow["salesOrg"] = salesOrg)
        (exportRow["currency"] = firstQuote?.Currency)
        (exportRow["buyingEntity"] = firstQuote?.SoldTo + " - " + soldToName)
        (exportRow["soldTo"] = firstQuote?.SoldTo)
        (exportRow["shipTo"] = firstQuote?.ShipTo)
        (exportRow["brand"] = brand ?: "")
        (exportRow["ph1"] = productHierarchy1 ?: "")
        (exportRow["ph2"] = productHierarchy2 ?: "")
        (exportRow["ph3"] = productHierarchy3 ?: "")
        (exportRow["material"] = material ?: "")
        (exportRow["materialLabel"] = materialDescription ?: "")
        (exportRow["legacyPartNo"] = legacyPart ?: "")
        (exportRow["customerMaterialNumber"] = customerMaterialNumber ?: "")
        (exportRow["origin"] = origin ?: "")
        (exportRow["deliveredLocation"] = deliveredLocation ?: "")
        (exportRow["freight"] = incotermsPerFreightPDF ?: "")
        (exportRow["SalesPerson"] = salesPerson?.customerId)
        (exportRow["SalesPersonEmail"] = salesPerson?.attribute3)
        if (showJobbers) (exportRow["jobbers"] = formatPrice(jobbers) ?: "")
        if (showJobbers) (exportRow["srp"] = formatPrice(srp) ?: "")
        if (showJobbers) (exportRow["map"] = formatPrice(map) ?: "")
        moqUomPdfExport = uomDescription?.get(moqUOM) ? uomDescription?.get(moqUOM) : moqUOM


        if (firstQuote?.PriceType == "3") {
            zbplItems = api.global.zbpl.get([salesOrg, pricelist, material])
            zbplCRItems = api.global.zbplCR.get([salesOrg, pricelist, material])

            zbplItem = zbplItems?.max { it.lastUpdateDate }
            zbplCRItem = zbplCRItems?.max { it.lastUpdateDate }

            if (zbplCRItem && zbplCRItem.lastUpdateDate > zbplItem?.lastUpdateDate?.toDate() && zbplCRItem.lastUpdateDate > api.global.ZBPLScales.get(zbplItem?.ConditionRecordNo)?.max { it.lastUpdateDate?.toDate() }?.lastUpdateDate?.toDate()) {
                possibleCRItems = zbplCRItems?.findAll {
                    it.attribute4 != "Delete" &&
                            it.attribute5 != "X" &&
                            it.validFrom <= pricingDate &&
                            it.validTo >= pricingDate
                }
                zbplCRItem = possibleCRItems?.max { it.lastUpdateDate }
                if (!zbplCRItem) continue

                effectiveDate = zbplCRItem?.validFrom ? outputFormat.format(zbplCRItem?.validFrom) : null

                price = zbplCRItem?.conditionValue
                priceUOM = zbplCRItem?.unitOfMeasure
                priceUomPdfExport = uomDescription?.get(priceUOM) ? uomDescription?.get(priceUOM) : priceUOM

                scales = zbplCRItem.attribute2 ? mapZBPLCRScales(zbplCRItem.attribute2) : null
                if (scales) {
                    scaleToMOQConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, zbplCRItem.attribute3, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
                    if (scaleToMOQConversionFactor == null) continue

                    firstScale = scales.findAll { moq >= it.ScaleQty * scaleToMOQConversionFactor }.max { it.ScaleQty * scaleToMOQConversionFactor }
                    validScales = scales.findAll { moq < it.ScaleQty * scaleToMOQConversionFactor }.collect {
                        it.ScaleQty *= scaleToMOQConversionFactor
                        it.ScaleUOM = moqUOM
                        it.Price += quoteFreightPrice
                        it
                    } ?: []
                    if (firstScale) {
                        firstScale.ScaleQty = moq
                        firstScale.ScaleUOM = moqUOM
                        firstScale.Price = firstScale.Price + quoteFreightPrice
                        validScales.add(firstScale)
                    } else {
                        validScales.add(getDefaultFirstScale(moq, moqUOM, price, priceUOM))
                    }
                    validScales.sort { it.ScaleQty }
                } else {
                    validScales = getDefaultValidScales(moq, moqUOM, price, priceUOM)
                }
            } else {
                possibleItems = zbplItems?.findAll {
                    it.ValidFrom <= pricingDate &&
                            it.ValidTo >= pricingDate
                }
                zbplItem = possibleItems?.max { it.lastUpdateDate }
                if (!zbplItem) continue

                effectiveDate = zbplItem?.ValidFrom ? outputFormat.format(zbplItem?.ValidFrom) : null

                price = zbplItem?.Amount
                priceUOM = zbplItem?.UnitOfMeasure
                priceUomPdfExport = uomDescription?.get(priceUOM) ? uomDescription?.get(priceUOM) : priceUOM

                scales = api.global.ZBPLScales.get(zbplItem?.ConditionRecordNo)
                if (scales) {
                    scaleToMOQConversionFactor = zbplItem?.ScaleUoM ? libs.QuoteLibrary.Conversion.getConversionFactor(material, zbplItem?.ScaleUoM, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal() : 1
                    if (scaleToMOQConversionFactor == null) continue

                    firstScale = scales.findAll { moq >= it.ScaleQty * scaleToMOQConversionFactor }.max { it.ScaleQty * scaleToMOQConversionFactor }
                    validScales = scales.findAll { moq < it.ScaleQty * scaleToMOQConversionFactor }.collect {
                        it.ScaleQty *= scaleToMOQConversionFactor
                        it.ScaleUOM = moqUOM
                        it.Price += quoteFreightPrice
                        it
                    } ?: []
                    if (firstScale) {
                        firstScale.ScaleQty = moq
                        firstScale.ScaleUOM = moqUOM
                        firstScale.Price = firstScale.Price + quoteFreightPrice
                        validScales.add(firstScale)
                    } else {
                        validScales.add(getDefaultFirstScale(moq, moqUOM, price, priceUOM))
                    }
                    validScales.sort { it.ScaleQty }
                } else {
                    validScales = getDefaultValidScales(moq, moqUOM, price, priceUOM)
                }
            }

            for (validScale in validScales) {
                scaleQty = validScale.ScaleQty
                scalePrice = per && validScale.Price ? validScale.Price / per : validScale.Price

                price = formatPrice(scalePrice?.toBigDecimal())

                moqPerUOMPDF = api.formatNumber("###,###.##", scaleQty) + " / " + moqUomPdfExport

                finalRow = exportRow + [
                        "effectiveDate" : effectiveDate,
                        "moq"           : scaleQty,
                        "moqUom"        : moqPerUOMPDF,
                        "price"         : price ?: "",
                        "priceUom"      : priceUomPdfExport ?: ""
                ] as Map<String, Object>

                api.isDebugMode() ? loader.add(finalRow) : loader.addRow(finalRow)
            }
        } else {
            priceUOM = firstQuote?.PricingUOM
            scales = api.global.quoteScales.get(firstQuote?.LineID)

            if (scales) {
                scaleToMOQConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, scales.find().ScaleUOM, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
                if (scaleToMOQConversionFactor == null) continue

                firstScale = scales.findAll { moq >= it.ScaleQty * scaleToMOQConversionFactor }.max { it.ScaleQty * scaleToMOQConversionFactor }
                validScales = scales.findAll { moq < it.ScaleQty * scaleToMOQConversionFactor }.collect {
                    it.ScaleQty *= scaleToMOQConversionFactor
                    it.ScaleUOM = moqUOM
                    it.Price += quoteFreightPrice
                    it
                } ?: []
                if (firstScale) {
                    firstScale.ScaleQty = moq
                    firstScale.ScaleUOM = moqUOM
                    firstScale.Price = firstScale.Price + quoteFreightPrice
                    validScales.add(firstScale)
                } else {
                    validScales.add(getDefaultFirstScale(moq, moqUOM, price, priceUOM))
                }
                validScales.sort { it.ScaleQty }
            } else {
                validScales = getDefaultValidScales(moq, moqUOM, price, priceUOM)
            }

            maxDate = getEffectiveDate(pricingDate, quotes?.collect { it.PriceValidFrom})
            for (validScale in validScales) {
                scaleQty = validScale.ScaleQty
                scalePrice = per && validScale.Price ? validScale.Price / per : validScale.Price
                scalePriceUOM = validScale.PriceUOM

                effectiveDate = maxDate ? outputFormat.format(maxDate) : null
                price = formatPrice(scalePrice?.toBigDecimal())
                priceUOM = scalePriceUOM
                priceUomPdfExport = uomDescription?.get(priceUOM) ? uomDescription?.get(priceUOM) : priceUOM

                moqPerUOMPDF = api.formatNumber("###,###.##", scaleQty) + " / " + moqUomPdfExport

                if (firstQuote?.PriceType != "4") {
                    finalRow = exportRow + [
                            "effectiveDate" : effectiveDate,
                            "moq"           : scaleQty,
                            "moqUom"        : moqPerUOMPDF,
                            "price"         : price ?: "",
                            "priceUom"      : priceUomPdfExport ?: ""
                    ] as Map<String, Object>

                    api.isDebugMode() ? loader.add(finalRow) : loader.addRow(finalRow)
                }
            }
        }
    }
}

if (api.isDebugMode()) api.trace("loader", loader)

def normalizeBigDecimal(BigDecimal x) {
    if (x == null) return null
    def n = x.stripTrailingZeros()

    return n.signum() == 0 ? BigDecimal.ZERO : n
}

String formatPrice(BigDecimal price) {
    if (!price) return price
    def parts = String.format('%.2f', price).split('\\.')
    def integerPart = parts[0].reverse().replaceAll(/(\d{3})(?=\d)/, '$1,').reverse()
    return "\$ ${integerPart}.${parts[1]}"
}

def getEffectiveDate(pricingDate, List priceValidFrom) {
    // Parse inputs as Date if they are strings
    def formattedPricingDate = pricingDate instanceof String ? Date.parse("yyyy-MM-dd", pricingDate) : pricingDate
    def formattedPriceValidFrom = priceValidFrom?.collect { it instanceof String ? Date.parse("yyyy-MM-dd", it) : it }

    if (!formattedPriceValidFrom) return null

    formattedPriceValidFrom = formattedPriceValidFrom.findAll { it <= formattedPricingDate }
    if (!formattedPriceValidFrom) return null

    return formattedPriceValidFrom.max()
}

List mapZBPLCRScales (crScales) {
    def scale
    return crScales?.split("\\|")?.collect {
        scale = it?.split("=")
        return [
                ScaleQty: scale[0]?.toBigDecimal() ?: null,
                Price   : scale[1]?.toBigDecimal() ?: null
        ]
    } ?: []
}

Map getDefaultFirstScale (moq, moqUOM, quotePrice, pricingUOM) {
    return [
            ScaleQty        : moq,
            ScaleUOM        : moqUOM,
            Price           : quotePrice,
            PriceUOM        : pricingUOM
    ]
}

List getDefaultValidScales (moq, moqUOM, quotePrice, pricingUOM) {
    return [
            [
                    ScaleQty        : moq,
                    ScaleUOM        : moqUOM,
                    Price           : quotePrice,
                    PriceUOM        : pricingUOM,
            ]
    ]
}