import java.text.SimpleDateFormat
import java.time.LocalDate

if (api.isInputGenerationExecution()) return

final dashboardName = "SPS"

def calcItem = dist.calcItem
def dashboard = api.isDebugMode() ? dashboardName : calcItem?.Value?.dashboard

if (dashboard != dashboardName) return

final dashboardQuery = libs.DashboardLibrary.Query

def outputFormat = new SimpleDateFormat("MM/dd/yyyy")
def outputFormatDateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

def loader = api.isDebugMode() ? [] : dist.dataLoader

def visited = new HashSet<>()

def plants = api.global.plant
def modeOfTransportations = api.global.modeOfTransportation
def customerMaster = api.global.customers
def products = api.global.products
def currencyDecimals = api.global.currencyDecimals
def uomDescription = api.global.uomDescription
def deliveredFreightTerms = libs.QuoteLibrary.Query.getAllDeliveredFreightTerms()

def quotes = out.LoadQuotes

List affectedVariants = dashboardQuery.findPendingAffectedVariantsByDashboard(dashboardName) ?: []
List variantsData = dashboardQuery.findSPSVariantsByNames(affectedVariants.Variant) ?: []
Map variantsDataMap = variantsData.collectEntries {
    [(it.key1): it]
} ?: [:]

Calendar cal = Calendar.getInstance()

LocalDate effectiveDateLD
def variant, filteredLines, filteredFreightLines, freightQuotes, additionalNotes, masterParent, label, customerForPDF,
        pricingDateOutput, showAdder
def pricingDate, effectiveDate, contracts, contractLines, materials, division, salesOrgs, soldTos, shipTos, pricelists
def exportRow, finalRow, salesOrg, material, pricelist, salesPerson, customerMaterialNumber, origin, deliveredLocation,
        modeOfSales, wpg, incoTermsPerFreightPDF, indexIndicator, productHierarchy1, productHierarchy2
def shipToCustomer, soldToName, shipToName, namedPlace, productPM, pricePerUOMPDFExport, quoteCurrencyDecimals,
        pricingUOM, priceUomPdfExport, maxDate, moqUomPdfExport, minQtyPerUOMPDFExport,
        scaleToMOQConversionFactor, quotePrice, quoteDeliveredPrice, quoteFreightPrice, scales, firstScale,
        freightAux, moq, moqUOM, validScales, pricePerUOMIsDelivered, scaleQty, scalePrice, scaleDeliveredPrice,
        scalePriceUOM, freightRow, conversionFactorDelivered, quotePer
def zbplItem, zbplCRItem, zbplItems, zbplCRItems, possibleItems, possibleCRItems
for (affectedVariant in affectedVariants) {
    variant = variantsDataMap[affectedVariant.Variant]
    if (!variant) continue

    def alphabet = ('A'..'W') + ['Y', 'Z']
    def combinations = []

    alphabet.each { firstLetter ->
        alphabet.each { secondLetter ->
            combinations << "$firstLetter$secondLetter"
        }
    }

    def entireAlphabet = alphabet + combinations

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

    if (!contracts && !contractLines && !materials && !division && !shipTos && !soldTos && !salesOrgs) continue

    if (contracts) filteredLines = filteredLines.findAll { it.SAPContractNumber in contracts }
    if (contractLines) filteredLines = filteredLines.findAll { it.SAPLineID in contractLines }
    if (materials) filteredLines = filteredLines.findAll { it.Material in materials }
    if (soldTos) filteredLines = filteredLines.findAll { it.SoldTo in soldTos }
    if (shipTos) filteredLines = filteredLines.findAll { it.ShipTo in shipTos }
    if (division) filteredLines = filteredLines.findAll { it.Division == division }
    if (salesOrgs) filteredLines = filteredLines.findAll { it.SalesOrg in salesOrgs }
    if (pricelists) filteredLines = filteredLines.findAll { it.PriceListPLT in pricelists }

    filteredFreightLines = filteredLines.findAll { row -> row.FreightValidFrom && row.FreightValidTo }
    filteredFreightLines = filteredFreightLines.findAll { row ->
        row.FreightValidFrom <= pricingDate && row.FreightValidTo >= pricingDate
    }

    freightQuotes = [:]
    filteredFreightLines?.each { it ->
        freightQuotes.putIfAbsent(it.SAPContractNumber + "|" + it.SAPLineID, it)
    }

    filteredLines = filteredLines.findAll { row -> row.PriceValidFrom && row.PriceValidTo }
    filteredLines = filteredLines.findAll { row ->
        row.PriceValidFrom <= pricingDate && row.PriceValidTo >= pricingDate
    }

    additionalNotes = api.global.footers?.get(affectedVariant.Variant + "|20")?.value

    label = variant.Label
    masterParent = variant.MasterParent
    customerForPDF = null
    if(label){
        customerForPDF = label
    } else if (masterParent){
        customerForPDF = api.find("C", 0, 1, "customerId", ["name"], Filter.equal("customerId", masterParent))?.find()?.name
    }

    pricingDateOutput = outputFormat.format(pricingDate)
    showAdder = variant.ShowAdder

    for (quote in filteredLines) {
        exportRow = [:]

        (exportRow["Variant"] = affectedVariant.Variant)
        (exportRow["EffectiveDateKey"] = affectedVariant.EffectiveDate)
        (exportRow["ChangeDate"] = affectedVariant.ChangeDate)
        (exportRow["Dashboard"] = dashboardName)
        (exportRow["UUID"] = affectedVariant.UUID)
        (exportRow["SPSKey"] = quote?.SAPContractNumber + "|" + quote?.SAPLineID)
        (exportRow["PBKey"] = "*")
        (exportRow["additionalNotes"] = additionalNotes ? api.jsonEncode(additionalNotes) : null)
        (exportRow["customer"] = customerForPDF)
        (exportRow["pricingDate"] = pricingDateOutput)
        (exportRow["showAdder"] = showAdder)

        salesOrg = quote?.SalesOrg
        material = quote?.Material
        pricelist = quote?.PriceListPLT

        indexIndicator = ""
        salesPerson = customerMaster.find{customer -> customer.customerId == quote.SalesPerson}
        shipToCustomer = customerMaster.find{customer -> customer.customerId == quote.ShipTo}
        soldToName = customerMaster.find{customer -> customer.customerId == quote.SoldTo}?.name
        shipToName = shipToCustomer?.name
        namedPlace = quote?.NamedPlace
        productPM = products?.find{product -> product.sku == material}

        freightRow = freightQuotes?.get(quote?.SAPContractNumber + "|" + quote?.SAPLineID)

        if(quote?.PriceType == "1" && entireAlphabet?.size() > 0){
            indexIndicator = entireAlphabet[0];
            entireAlphabet.remove(0)
        }

        if(quote?.PriceType == "2" && quote?.IndexIndicator && entireAlphabet?.size() > 0){
            indexIndicator = "X"
        }

        quoteCurrencyDecimals = currencyDecimals?.get(quote?.Currency)

        customerMaterialNumber = [quote.CustomerMaterial, quote.ThirdPartyCustomer]?.findAll{it != null}?.join(" / ")
        origin = plants?.get(quote.Plant)
        if (shipToName?.toLowerCase()?.contains("generic")) {
            deliveredLocation = "MULTIPLE"
        } else {
            deliveredLocation = [shipToCustomer?.name, shipToCustomer?.attribute5, shipToCustomer?.attribute7, shipToCustomer?.attribute4]?.findAll{it != null}?.join(", ")
        }
        modeOfSales = modeOfTransportations?.get(quote.ModeOfTransportation)
        wpg = productPM?.attribute4
        productHierarchy2 = productPM?.attribute16
        productHierarchy1 = productPM?.attribute14

        incoTermsPerFreightPDF = quote?.Incoterm + " / \n" + quote?.FreightTermValue + (namedPlace ? " / \n" + namedPlace : "")

        (exportRow["salesOrg"] = salesOrg)
        (exportRow["material"] = material)
        (exportRow["modeOfTransportation"] = quote.ModeOfTransportation)
        (exportRow["meansOfTransportation"] = quote.MeansOfTransportation)
        (exportRow["salesRepName"] = salesPerson?.name)
        (exportRow["SalesPerson"] = salesPerson?.customerId)
        (exportRow["SalesPersonEmail"] = salesPerson?.attribute3)
        (exportRow["materialLabel"] = quote.MaterialDescription)
        (exportRow["materialAndLabel"] = quote.Material + " / " + quote.MaterialDescription)
        (exportRow["customerMaterialNumber"] = customerMaterialNumber)
        (exportRow["origin"] = origin)
        (exportRow["deliveredLocation"] = deliveredLocation)
        (exportRow["modeOfSale"] = modeOfSales)
        (exportRow["wpg"] = wpg)
        (exportRow["freight"] = incoTermsPerFreightPDF)
        (exportRow["index"] = indexIndicator)
        (exportRow["indexNumberOne"] = quote?.IndexNumberOne)
        (exportRow["indexNumberTwo"] = quote?.IndexNumberTwo)
        (exportRow["indexNumberThree"] = quote?.IndexNumberThree)
        (exportRow["adder"] = quote?.Adder)
        (exportRow["adderUOM"] = uomDescription[quote?.AdderUOM]?:quote?.AdderUOM)
        (exportRow["recalculationDate"] = quote?.RecalculationDate)
        (exportRow["recalculationPeriod"] = quote?.RecalculationPeriod)
        (exportRow["referencePeriod"] = quote?.ReferencePeriodValue)
        (exportRow["currency"] = quote?.Currency)
        (exportRow["buyingEntity"] = quote?.SoldTo + " - " + soldToName)
        (exportRow["ph1"] = productHierarchy1)
        (exportRow["ph2"] = productHierarchy2)

        quotePer = quote?.Per
        quotePrice = quote?.Price && quotePer ? quote?.Price / quotePer : quote?.Price
        quoteFreightPrice = freightRow?.FreightAmount ?: BigDecimal.ZERO
        conversionFactorDelivered = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightRow?.FreightUOM, quote?.PricingUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
        quoteDeliveredPrice = quote?.DeliveredPrice && quotePer ? quote?.DeliveredPrice / quotePer : quote?.Price
        freightAux = conversionFactorDelivered != null ? quoteFreightPrice * conversionFactorDelivered : quoteFreightPrice

        moq = quote?.MOQ
        moqUOM = quote?.MOQUOM
        moqUomPdfExport = uomDescription?.get(moqUOM) ? uomDescription?.get(moqUOM) : moqUOM

        if (quote?.PriceType == "3") {
            zbplItems = api.global.zbpl.get([salesOrg, pricelist, material])
            zbplCRItems = api.global.zbplCR.get([salesOrg, pricelist, material])

            zbplItem = zbplItems?.max { it.lastUpdateDate }
            zbplCRItem = zbplCRItems?.max { it.lastUpdateDate }

            if (zbplCRItem && zbplCRItem.lastUpdateDate > zbplItem?.lastUpdateDate?.toDate() && zbplCRItem.lastUpdateDate > api.global.ZBPLScales.get(zbplItem.ConditionRecordNo)?.max { it.lastUpdateDate?.toDate() }?.lastUpdateDate?.toDate()) {
                possibleCRItems = zbplCRItems?.findAll {
                    it.attribute4 != "Delete" &&
                            it.attribute5 != "X" &&
                            it.validFrom <= pricingDate &&
                            it.validTo >= pricingDate
                }
                zbplCRItem = possibleCRItems?.max { it.lastUpdateDate }
                if (!zbplCRItem) continue

                effectiveDate = zbplCRItem?.validFrom ? outputFormatDateTime.format(zbplCRItem?.validFrom) : null

                pricingUOM = zbplCRItem?.unitOfMeasure
                priceUomPdfExport = uomDescription?.get(pricingUOM) ? uomDescription?.get(pricingUOM) : pricingUOM

                conversionFactorDelivered = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightRow?.FreightUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
                freightAux = conversionFactorDelivered != null ? quoteFreightPrice * conversionFactorDelivered : quoteFreightPrice

                scales = zbplCRItem.attribute2 ? mapZBPLCRScales(zbplCRItem.attribute2) : null
                if (scales) {
                    scaleToMOQConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, zbplCRItem.attribute3, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
                    if (scaleToMOQConversionFactor == null) continue

                    firstScale = scales.findAll { moq >= it.ScaleQty * scaleToMOQConversionFactor }.max { it.ScaleQty * scaleToMOQConversionFactor }
                    validScales = scales.findAll { moq < it.ScaleQty * scaleToMOQConversionFactor }.collect {
                        it.ScaleQty *= scaleToMOQConversionFactor
                        it.ScaleUOM = moqUOM
                        it
                    } ?: []
                    if (firstScale) {
                        firstScale.ScaleQty = moq
                        firstScale.ScaleUOM = moqUOM
                        validScales.add(firstScale)
                    } else {
                        validScales.add(getDefaultFirstScale(moq, moqUOM, quotePrice, pricingUOM))
                    }
                    validScales = validScales.collect {
                        it.DeliveredPrice = freightAux != null && conversionFactorDelivered != null ? it.Price + freightAux : it.Price
                        it
                    }
                    validScales.sort { it.ScaleQty }
                } else {
                    validScales = getDefaultValidScales(moq, moqUOM, quotePrice, pricingUOM, quoteDeliveredPrice)
                }
            } else {
                possibleItems = zbplItems?.findAll {
                    it.ValidFrom <= pricingDate &&
                            it.ValidTo >= pricingDate
                }
                zbplItem = possibleItems?.max { it.lastUpdateDate }
                if (!zbplItem) continue

                effectiveDate = zbplItem?.ValidFrom ? outputFormatDateTime.format(zbplItem?.ValidFrom) : null

                pricingUOM = zbplItem?.UnitOfMeasure
                priceUomPdfExport = uomDescription?.get(pricingUOM) ? uomDescription?.get(pricingUOM) : pricingUOM

                conversionFactorDelivered = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightRow?.FreightUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
                freightAux = conversionFactorDelivered != null ? quoteFreightPrice * conversionFactorDelivered : quoteFreightPrice

                scales = api.global.ZBPLScales.get(zbplItem.ConditionRecordNo)
                if (scales) {
                    scaleToMOQConversionFactor = zbplItem.ScaleUoM ? libs.QuoteLibrary.Conversion.getConversionFactor(material, zbplItem.ScaleUoM, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal() : 1
                    if (scaleToMOQConversionFactor == null) continue

                    firstScale = scales.findAll { moq >= it.ScaleQty * scaleToMOQConversionFactor }.max { it.ScaleQty * scaleToMOQConversionFactor }
                    validScales = scales.findAll { moq < it.ScaleQty * scaleToMOQConversionFactor }.collect {
                        it.ScaleQty *= scaleToMOQConversionFactor
                        it.ScaleUOM = moqUOM
                        it
                    } ?: []
                    if (firstScale) {
                        firstScale.ScaleQty = moq
                        firstScale.ScaleUOM = moqUOM
                        validScales.add(firstScale)
                    } else {
                        validScales.add(getDefaultFirstScale(moq, moqUOM, quotePrice, pricingUOM))
                    }
                    validScales = validScales.collect {
                        it.DeliveredPrice = freightAux != null && conversionFactorDelivered != null ? it.Price + freightAux : it.Price
                        it
                    }
                    validScales.sort { it.ScaleQty }
                } else {
                    validScales = getDefaultValidScales(moq, moqUOM, quotePrice, pricingUOM, quoteDeliveredPrice)
                }
            }

            for (validScale in validScales) {
                scaleQty = validScale.ScaleQty
                scaleDeliveredPrice = validScale.DeliveredPrice

                pricePerUOMPDFExport = getFormattedPrice(scaleDeliveredPrice, quoteCurrencyDecimals, quotePer) + " / " + priceUomPdfExport

                minQtyPerUOMPDFExport = api.formatNumber("###,###.##", scaleQty) + " / " + moqUomPdfExport

                finalRow = exportRow + [
                        "effectiveDate" : effectiveDate,
                        "moqUom"        : minQtyPerUOMPDFExport,
                        "priceUom"      : pricePerUOMPDFExport
                ] as Map<String, Object>

                api.isDebugMode() ? loader.add(finalRow) : loader.addRow(finalRow)
            }
        } else {
            pricingUOM = quote?.PricingUOM
            scales = api.global.quoteScales.get(quote.LineID)

            if (scales) {
                scaleToMOQConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, scales.find().ScaleUOM, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
                if (scaleToMOQConversionFactor == null) continue

                firstScale = scales.findAll { moq >= it.ScaleQty * scaleToMOQConversionFactor }.max { it.ScaleQty * scaleToMOQConversionFactor }
                validScales = scales.findAll { moq < it.ScaleQty * scaleToMOQConversionFactor }.collect {
                    it.ScaleQty *= scaleToMOQConversionFactor
                    it.ScaleUOM = moqUOM
                    it
                } ?: []
                if (firstScale) {
                    firstScale.ScaleQty = moq
                    firstScale.ScaleUOM = moqUOM
                    validScales.add(firstScale)
                } else {
                    validScales.add(getDefaultFirstScale(moq, moqUOM, quotePrice, pricingUOM))
                }
                validScales = validScales.collect {
                    it.DeliveredPrice = it.Price != null && freightAux != null && conversionFactorDelivered != null ? it.Price + freightAux : it.Price
                    it
                }
                validScales.sort { it.ScaleQty }
            } else {
                validScales = getDefaultValidScales(moq, moqUOM, quotePrice, pricingUOM, quoteDeliveredPrice)
            }

            maxDate = getEffectiveDate(pricingDate, quote?.PriceValidFrom?.toString(), freightRow?.FreightValidFrom?.toString())
            pricePerUOMIsDelivered = quote?.FreightTerm in deliveredFreightTerms && maxDate
            for (validScale in validScales) {
                scaleQty = validScale.ScaleQty
                scalePrice = validScale.Price
                scaleDeliveredPrice = validScale.DeliveredPrice
                scalePriceUOM = validScale.PriceUOM
                priceUomPdfExport = uomDescription?.get(scalePriceUOM) ? uomDescription?.get(scalePriceUOM) : scalePriceUOM
                if (pricePerUOMIsDelivered) {
                    effectiveDate = maxDate ? outputFormat.format(maxDate) : null
                    pricePerUOMPDFExport = getFormattedPrice(scaleDeliveredPrice, quoteCurrencyDecimals, quotePer) + " / " + priceUomPdfExport
                } else {
                    effectiveDate = quote?.PriceValidFrom ? outputFormat.format(quote?.PriceValidFrom) : null
                    pricePerUOMPDFExport = getFormattedPrice(scalePrice, quoteCurrencyDecimals, quotePer) + " / " + priceUomPdfExport
                }

                minQtyPerUOMPDFExport = api.formatNumber("###,###.##", scaleQty) + " / " + moqUomPdfExport

                if (quote?.PriceType != "4") {
                    finalRow = exportRow + [
                            "effectiveDate" : effectiveDate,
                            "moqUom"        : minQtyPerUOMPDFExport,
                            "priceUom"      : pricePerUOMPDFExport
                    ] as Map<String, Object>

                    api.isDebugMode() ? loader.add(finalRow) : loader.addRow(finalRow)
                }
            }
        }
    }
}

if (api.isDebugMode()) api.trace("loader", loader)

def getFormattedPrice(price, numberOfDecimals, per = null) {
    if(!price) return null
    if(per != 1) return price
    switch (numberOfDecimals){
        case 2:
            return api.formatNumber("#####0.00", price)
        case 3:
            return api.formatNumber("#####0.000", price)
        case 4:
            return api.formatNumber("#####0.0000", price)
        default:
            return api.formatNumber("#####0.00", price)
    }
}

def getEffectiveDate(pricingDate, priceValidFrom, freightValidFrom) {
    // Parse inputs as Date if they are strings
    pricingDate = pricingDate instanceof String ? Date.parse("yyyy-MM-dd", pricingDate) : pricingDate
    priceValidFrom = priceValidFrom instanceof String ? Date.parse("yyyy-MM-dd", priceValidFrom) : priceValidFrom
    freightValidFrom = freightValidFrom instanceof String ? Date.parse("yyyy-MM-dd", freightValidFrom) : freightValidFrom

    // Handle cases where one or both dates may be null
    if (priceValidFrom == null && freightValidFrom == null) return null
    if (priceValidFrom == null) return freightValidFrom
    if (freightValidFrom == null) return priceValidFrom

    // Calculate the difference in days
    def priceDiff = (pricingDate.time - priceValidFrom.time) / (1000 * 60 * 60 * 24)
    def freightDiff = (pricingDate.time - freightValidFrom.time) / (1000 * 60 * 60 * 24)

    // Return the date that is closest but not in the future
    if (priceDiff >= 0 && freightDiff >= 0) {
        return priceDiff <= freightDiff ? priceValidFrom : freightValidFrom
    } else if (priceDiff >= 0) {
        return priceValidFrom
    } else if (freightDiff >= 0) {
        return freightValidFrom
    }
    return null // If both dates are in the future, return null
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

List getDefaultValidScales (moq, moqUOM, quotePrice, pricingUOM, quoteDeliveredPrice) {
    return [
            [
                    ScaleQty        : moq,
                    ScaleUOM        : moqUOM,
                    Price           : quotePrice,
                    PriceUOM        : pricingUOM,
                    DeliveredPrice  : quoteDeliveredPrice
            ]
    ]
}