import net.pricefx.common.api.FieldFormatType

import java.text.SimpleDateFormat

def createCalendarFromDateString(dateString, format = "yyyy-MM-dd") {
    if(!dateString) return
    def dateFormat = new SimpleDateFormat(format)
    def outputFormat = new SimpleDateFormat("MM/dd/yyyy")
    def date = dateFormat.parse(dateString) // Parse the string into a Date object
    def calendar = Calendar.getInstance()
    calendar.time = date // Set the Calendar's time to the parsed date
    return outputFormat.format(calendar.time)
}

def visited = new HashSet<>()
def constants = libs.DashboardConstantsLibrary.PricePublishing

def plants = api.global.plant
def modeOfTransportations = api.global.modeOfTransportation
def customerMaster = api.global.customers
def products = api.global.products
def currencyDecimals = api.global.currencyDecimals
def uomDescription = api.global.uomDescription
def salesData = out.LoadSalesData
def deliveredFreightTerms = libs.QuoteLibrary.Query.getAllDeliveredFreightTerms()

def warnings = []

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
def outputFormat = new SimpleDateFormat("MM/dd/yyyy")
def inputFormat = new SimpleDateFormat("yyyy-MM-dd")
def inputFormatDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
def zbplFormatDateTimeInput = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
def outputFormatDateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

String pricingDate = out.Filters?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY)//"2025-02-10"

def columnLabels = [
        SalesRepName: constants.SALES_REP_NAME,
        ProductCodeDescription: constants.PRODUCT_CODE_DESCRIPTION,
        CustomerMaterialNumber: constants.CUSTOMER_MATERIAL_NUMBER,
        Origin: constants.ORIGIN,
        DeliveredLocation: constants.DELIVERED_LOCATION,
        ModeOfSales: constants.MODE_OF_SALES,
        EffectiveDate: constants.EFFECTIVE_DATE,
        WPG: constants.WPG,
        MinQtyPerUOM: constants.MIN_QTY_PER_UOM,
        PricePerUOM: constants.PRICE_PER_UOM,
        IncoTermsPerFreight: constants.INCOTERMS_PER_FREIGHT,
        IndexIndicator: constants.INDEX_INDICATOR,
        PriceType: constants.PriceType,
        ContractNumber: constants.ContractNumber,
        QuoteId: constants.QuoteId,
        ContractItem: constants.ContractItem,
        SalesOrg: constants.SalesOrg,
        Division: constants.Division,
        Pricelist: constants.Pricelist,
        QuoteLastUpdate: constants.QuoteLastUpdate,
        "SoldTo": constants.SOLD_TO,
        "SoldToName": constants.SOLD_TO_NAME,
        "ShipTo": constants.SHIP_TO,
        "ShipToName": constants.SHIP_TO_NAME,
        "CustomerGroup": constants.CUSTOMER_GROUP,
        "ShipToIndustry": constants.SHIP_TO_INDUSTRY,
        "ExpirationDate": constants.EXPIRATION_DATE,
        "NetWeightUOM": constants.NET_WEIGHT_UOM,
        "OriginPrice": constants.ORIGIN_PRICE,
        "FreightPrice": constants.FREIGHT_PRICE,
        "DeliveredPrice": constants.DELIVERED_PRICE,
        "Currency": constants.CURRENCY,
        "OriginStandardUomPrice": constants.ORIGIN_STANDARD_UOM_PRICE,
        "FreightStandardUomPrice": constants.FREIGHT_STANDARD_UOM_PRICE,
        "DeliveredStandardUomPrice": constants.DELIVERED_STANDARD_UOM_PRICE,
        "StandardUom": constants.STANDARD_UOM,
        "SalesRepNumber": constants.SALES_REP_NUMBER,
        "ShippingPointReceivingPt": constants.SHIPPPING_POINT_RECEIVING_PT,
        "ProductHierarchy": constants.PRODUCT_HIERARCHY,
        "ProductHierarchyLevel2": constants.PRODUCT_HIERARCHY_LEVEL_2,
        "SoldToIncludedInExclusionTable": constants.SOLD_TO_INCLUDED_IN_EXCLUSION_TABLE,
        "CommentFromExclusionTable": constants.COMMENT_FROM_EXCLUSION_TABLE,
        "FreightTerm": constants.FREIGHT_TERM,
        "FreightValidFrom": constants.FREIGHT_VALID_FROM,
        "FreightValidTo": constants.FREIGHT_VALID_TO,
        "ShipToCity": constants.SHIP_TO_CITY,
        "ShipToZip": constants.SHIP_TO_ZIP,
        "ShipToCountry": constants.SHIP_TO_COUNTRY,
        "ShipToState": constants.SHIP_TO_STATE,
        "NamedPlace": constants.NAMED_PLACE,
]

def summary = api.newMatrix(columnLabels.collect({ column -> column.value }))

def alphabet = ('A'..'W') + ['Y', 'Z']  // A list of single letters from A to Z
def combinations = []

// Generate all combinations from AA to ZZ
alphabet.each { firstLetter ->
    alphabet.each { secondLetter ->
        combinations << "$firstLetter$secondLetter"
    }
}

def entireAlphabet = alphabet + combinations

def exportPdf = []

def groupedQuotes = api.local.quotes as List
def freightQuotes = api.local.freightQuotes as Map
def row, exportRow, indexIndicator, salesPerson, shipToCustomer, soldToName, shipToName, shipToIndustry, shipToCity,
        shipToState, shipToZip, shipToCountry, namedPlace, productPM, salesOrg, material, pricelist, pricePerUOM,
        pricePerUOMPDFExport, effectiveDate, quoteCurrencyDecimals, validFromFormatted, pricingUOM, priceUomPdfExport,
        maxDate, salesPersonName, productCodeAndDescription, customerMaterialNumber, origin, deliveredLocation, modeOfSales,
        customerGroup, wpg, netWeightUOM, unitOfMeasure, productHierarchy, productHierarchy2, productHierarchy1,
        moqUomPdfExport, minQtyPerUOM, minQtyPerUOMPDFExport, incoTermsPerFreight, incoTermsPerFreightPDF, scaleToMOQConversionFactor,
        priceToScaleConversionFactor, conversionFactor, conversionFactorFreight, standardPrice, standardDeliveredPrice, standardFreightPrice,
        exclusion, newQuoteLastUpdateDate, quotePrice, quoteDeliveredPrice, quoteFreightPrice, scales, firstScale, freightAux,
        moq, moqUOM, validScales, pricePerUOMIsDelivered, scaleQty, scalePrice, scaleDeliveredPrice, scalePriceUOM, freightRow, conversionFactorDelivered, quotePer
def zbplItem, zbplCRItem, zbplItems, zbplCRItems, possibleItems, possibleCRItems
for(quote in groupedQuotes) {
    row = [:]
    exportRow = [:]

    salesOrg = quote?.SalesOrg
    material = quote?.Material
    pricelist = quote?.PriceListPLT

    indexIndicator = ""
    salesPerson = customerMaster.find{customer -> customer.customerId == quote.SalesPerson}
    shipToCustomer = customerMaster.find{customer -> customer.customerId == quote.ShipTo}
    soldToName = customerMaster.find{customer -> customer.customerId == quote.SoldTo}?.name
    shipToName = shipToCustomer?.name
    shipToIndustry = shipToCustomer?.attribute12
    shipToCity = shipToCustomer?.attribute5
    shipToState = shipToCustomer?.attribute7
    shipToZip = shipToCustomer?.attribute6
    shipToCountry = shipToCustomer?.attribute4
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

    salesPersonName = salesPerson?.name
    productCodeAndDescription = quote.Material + " / " + productPM?.label
    customerMaterialNumber = [quote.CustomerMaterial, quote.ThirdPartyCustomer]?.findAll{it != null}?.join(" / ")
    origin = plants?.get(quote.Plant)
    if (shipToName?.toLowerCase()?.contains("generic")) {
        deliveredLocation = "MULTIPLE"
    } else {
        deliveredLocation = [shipToCustomer?.name, shipToCustomer?.attribute5, shipToCustomer?.attribute7, shipToCustomer?.attribute4]?.findAll{it != null}?.join(", ")
    }
    modeOfSales = modeOfTransportations?.get(quote.ModeOfTransportation)
    customerGroup = salesData?.find { it.customerId == quote?.SoldTo }?.attribute6
    wpg = productPM?.attribute4
    netWeightUOM = productPM?.attribute5
    unitOfMeasure = productPM?.unitOfMeasure
    productHierarchy = productPM?.attribute1
    productHierarchy2 = productPM?.attribute16
    productHierarchy1 = productPM?.attribute14

    incoTermsPerFreight = quote?.Incoterm + " / " + quote?.FreightTermValue + (namedPlace ? " / " + namedPlace : "")
    incoTermsPerFreightPDF = quote?.Incoterm + " / \n" + quote?.FreightTermValue + (namedPlace ? " / \n" + namedPlace : "")

    exclusion = findExclusionForLine(quote?.SoldTo as String, quote?.ShipTo as String, productHierarchy1 as String, material as String)

    newQuoteLastUpdateDate = quote?.QuoteLastUpdate ? inputFormatDateTime.parse(quote?.QuoteLastUpdate?.toString()) : null

    (row[columnLabels."SalesRepName"] = salesPersonName)
    (row[columnLabels."ProductCodeDescription"] = productCodeAndDescription)
    (row[columnLabels."CustomerMaterialNumber"] = customerMaterialNumber)
    (row[columnLabels."Origin"] = origin)
    (row[columnLabels."DeliveredLocation"] = deliveredLocation)
    (row[columnLabels."ModeOfSales"] = modeOfSales)
    (row[columnLabels."WPG"] = wpg)
    (row[columnLabels."IncoTermsPerFreight"] = incoTermsPerFreight)
    (row[columnLabels."IndexIndicator"] = indexIndicator)
    (row[columnLabels."PriceType"] = quote?.PriceType)
    (row[columnLabels."QuoteId"] = quote?.QuoteID)
    (row[columnLabels."ContractNumber"] = quote?.SAPContractNumber)
    (row[columnLabels."Pricelist"] = pricelist)
    (row[columnLabels."ContractItem"] = quote?.SAPLineID)
    (row[columnLabels."SalesOrg"] = salesOrg)
    (row[columnLabels."Division"] = quote?.Division)
    (row[columnLabels."QuoteLastUpdate"] = newQuoteLastUpdateDate ? outputFormatDateTime.format(newQuoteLastUpdateDate) : null)
    (row[columnLabels."SoldTo"] = quote?.SoldTo)
    (row[columnLabels."SoldToName"] = soldToName)
    (row[columnLabels."ShipTo"] = quote?.ShipTo)
    (row[columnLabels."ShipToName"] = shipToName)
    (row[columnLabels."CustomerGroup"] = customerGroup)
    (row[columnLabels."ShipToIndustry"] = shipToIndustry)
    (row[columnLabels."ExpirationDate"] = quote?.PriceValidTo ? outputFormat.format(quote?.PriceValidTo) : null)
    (row[columnLabels."NetWeightUOM"] = netWeightUOM)
    (row[columnLabels."Currency"] = quote?.Currency)
    (row[columnLabels."StandardUom"] = unitOfMeasure)
    (row[columnLabels."SalesRepNumber"] = quote?.SalesPerson)
    (row[columnLabels."ShippingPointReceivingPt"] = quote?.ShippingPoint)
    (row[columnLabels."ProductHierarchy"] = productHierarchy)
    (row[columnLabels."ProductHierarchyLevel2"] = productHierarchy2)
    (row[columnLabels."SoldToIncludedInExclusionTable"] = out.LoadExclusionCPT?.get(quote?.SoldTo?.toString()) ? "X" : "")
    (row[columnLabels."CommentFromExclusionTable"] = exclusion?.attribute6)
    (row[columnLabels."FreightTerm"] = quote?.FreightTerm)
    (row[columnLabels."FreightValidFrom"] = freightRow?.FreightValidFrom ? outputFormat.format(freightRow?.FreightValidFrom) : "")
    (row[columnLabels."FreightValidTo"] = freightRow?.FreightValidTo ? outputFormat.format(freightRow?.FreightValidTo) : "")
    (row[columnLabels."ShipToCity"] = shipToCity)
    (row[columnLabels."ShipToZip"] = shipToZip)
    (row[columnLabels."ShipToState"] = shipToState)
    (row[columnLabels."ShipToCountry"] = shipToCountry)
    (row[columnLabels."NamedPlace"] = namedPlace)


    (exportRow["salesOrg"] = salesOrg)
    (exportRow["material"] = quote.Material)
    (exportRow["modeOfTransportation"] = quote.ModeOfTransportation)
    (exportRow["meansOfTransportation"] = quote.MeansOfTransportation)
    (exportRow["salesRepName"] = salesPerson?.name)
    (exportRow["material"] = quote.Material)
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
    (exportRow["origin"] = origin)
    (exportRow["ph1"] = productHierarchy1)
    (exportRow["ph2"] = productHierarchy2)

    quotePer = quote?.Per
    quotePrice = quote?.Price && quotePer ? quote?.Price / quotePer : quote?.Price
    quoteFreightPrice = freightRow?.FreightAmount ?: BigDecimal.ZERO
    conversionFactorDelivered = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightRow?.FreightUOM, quote?.PricingUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
    quoteDeliveredPrice = quote?.DeliveredPrice && quotePer ? quote?.DeliveredPrice / quotePer : quote?.Price
    freightAux = conversionFactorDelivered != null ? quoteFreightPrice * conversionFactorDelivered : quoteFreightPrice

    if (conversionFactorDelivered == null && quoteFreightPrice) warnings.add("Missing 'UOM conversion' from Freight UOM (${freightRow?.FreightUOM}) to Pricing UOM (${quote?.PricingUOM}) for material ${material}")

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
                        it.validFrom <= sdf.parse(pricingDate) &&
                        it.validTo >= sdf.parse(pricingDate)
            }
            zbplCRItem = possibleCRItems?.max { it.lastUpdateDate }
            if (!zbplCRItem) continue

            effectiveDate = zbplCRItem?.validFrom ? outputFormatDateTime.format(zbplCRItem?.validFrom) : null

            pricingUOM = zbplCRItem?.unitOfMeasure
            priceUomPdfExport = uomDescription?.get(pricingUOM) ? uomDescription?.get(pricingUOM) : pricingUOM

            conversionFactorDelivered = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightRow?.FreightUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
            freightAux = conversionFactorDelivered != null ? quoteFreightPrice * conversionFactorDelivered : quoteFreightPrice
            if (conversionFactorDelivered == null && quoteFreightPrice) warnings.add("Missing 'UOM conversion' from Freight UOM (${freightRow?.FreightUOM}) to Pricing UOM (${quote?.PricingUOM}) for material ${material}")

            scales = zbplCRItem.attribute2 ? mapZBPLCRScales(zbplCRItem.attribute2) : null
            if (scales) {
                scaleToMOQConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, zbplCRItem.attribute3, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
                if (scaleToMOQConversionFactor == null) api.throwException("Missing conversion from ZBPL CR Scale UOM (${zbplCRItem.attribute3}) to MOQ UOM (${moqUOM}) for material ${material}")

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
                it.ValidFrom <= sdf.parse(pricingDate) &&
                        it.ValidTo >= sdf.parse(pricingDate)
            }
            zbplItem = possibleItems?.max { it.lastUpdateDate }
            if (!zbplItem) continue

            effectiveDate = zbplItem?.ValidFrom ? outputFormatDateTime.format(zbplItem?.ValidFrom) : null

            pricingUOM = zbplItem?.UnitOfMeasure
            priceUomPdfExport = uomDescription?.get(pricingUOM) ? uomDescription?.get(pricingUOM) : pricingUOM

            conversionFactorDelivered = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightRow?.FreightUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
            freightAux = conversionFactorDelivered != null ? quoteFreightPrice * conversionFactorDelivered : quoteFreightPrice
            if (conversionFactorDelivered == null && quoteFreightPrice) warnings.add("Missing 'UOM conversion' from Freight UOM (${freightRow?.FreightUOM}) to Pricing UOM (${pricingUOM}) for material ${material}")

            scales = api.global.ZBPLScales.get(zbplItem.ConditionRecordNo)
            if (scales) {
                //TODO ask if ScaleUoM will be filled, what to do when is null?
                scaleToMOQConversionFactor = zbplItem.ScaleUoM ? libs.QuoteLibrary.Conversion.getConversionFactor(material, zbplItem.ScaleUoM, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal() : 1
                if (scaleToMOQConversionFactor == null) api.throwException("Missing conversion from ZBPL Scale UOM (${zbplItem.ScaleUoM}) to MOQ UOM (${moqUOM}) for material ${material}")

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

        conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, pricingUOM, unitOfMeasure, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
        conversionFactorFreight = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightRow?.FreightUOM, unitOfMeasure, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
        if (conversionFactor == null) warnings.add("Missing 'UOM conversion' from Pricing UOM (${pricingUOM}) to Product Master UOM (${unitOfMeasure}) for material ${material}")
        if (conversionFactorFreight == null && quoteFreightPrice) warnings.add("Missing 'UOM conversion' from Freight UOM (${freightRow?.FreightUOM}) to Product Master UOM (${unitOfMeasure}) for material ${material}")

        for (validScale in validScales) {
            scaleQty = validScale.ScaleQty
            scalePrice = validScale.Price
            scaleDeliveredPrice = validScale.DeliveredPrice

            pricePerUOM = getFormattedPrice(scalePrice, quoteCurrencyDecimals, quotePer) + " / " + pricingUOM
            pricePerUOMPDFExport = getFormattedPrice(scaleDeliveredPrice, quoteCurrencyDecimals, quotePer) + " / " + priceUomPdfExport

            minQtyPerUOM = api.formatNumber("###,###.##", scaleQty) + " / " + moqUOM
            minQtyPerUOMPDFExport = api.formatNumber("###,###.##", scaleQty) + " / " + moqUomPdfExport

            standardPrice = scalePrice != null && conversionFactor != null ? getFormattedPrice(scalePrice * conversionFactor, quoteCurrencyDecimals, quotePer) : null
            standardDeliveredPrice = scaleDeliveredPrice != null && conversionFactor != null ? getFormattedPrice(scaleDeliveredPrice * conversionFactor, quoteCurrencyDecimals, quotePer) : null
            standardFreightPrice = quoteFreightPrice != null && conversionFactorFreight != null ? getFormattedPrice(quoteFreightPrice * conversionFactorFreight, quoteCurrencyDecimals) : null

            exportPdf.add(
                    exportRow + [
                            "effectiveDate" : effectiveDate,
                            "moqUom"        : minQtyPerUOMPDFExport,
                            "priceUom"      : pricePerUOMPDFExport
                    ] as Map<String, Object>
            ) //TODO CHECK
            summary.addRow(
                    row + [
                            (columnLabels."EffectiveDate")              : effectiveDate,
                            (columnLabels."MinQtyPerUOM")               : minQtyPerUOM,
                            (columnLabels."PricePerUOM")                : pricePerUOM,
                            (columnLabels."OriginPrice")                : getFormattedPrice(scalePrice, quoteCurrencyDecimals, quotePer),
                            (columnLabels."FreightPrice")               : quoteFreightPrice != null ? getFormattedPrice(quoteFreightPrice, quoteCurrencyDecimals) : "",
                            (columnLabels."DeliveredPrice")             : getFormattedPrice(scaleDeliveredPrice, quoteCurrencyDecimals, quotePer),
                            (columnLabels."OriginStandardUomPrice")     : standardPrice,
                            (columnLabels."FreightStandardUomPrice")    : standardFreightPrice,
                            (columnLabels."DeliveredStandardUomPrice")  : standardDeliveredPrice
                    ] as Map<String, Object>
            )
        }
    } else {
        pricingUOM = quote?.PricingUOM
        scales = api.local.quoteScales.get(quote.LineID)

        if (scales) {
            if (quoteFreightPrice) {
                priceToScaleConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, pricingUOM, scales.find().PriceUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
                if (priceToScaleConversionFactor == null) api.throwException("Missing conversion from Pricing UOM (${pricingUOM}) to Scale Price UOM (${scales.find().PriceUOM}) for material ${material}")
                quoteFreightPrice *= priceToScaleConversionFactor
            }

            scaleToMOQConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, scales.find().ScaleUOM, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
            if (scaleToMOQConversionFactor == null) api.throwException("Missing conversion from Scale UOM (${scales.find().ScaleUOM}) to MOQ UOM (${moqUOM}) for material ${material}")

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
        conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, validScales.find().PriceUOM, unitOfMeasure, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
        conversionFactorFreight = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightRow?.FreightUOM, unitOfMeasure, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
        if (conversionFactor == null) warnings.add("Missing 'UOM conversion' from Pricing UOM (${pricingUOM}) to Product Master UOM (${unitOfMeasure}) for material ${material}")
        if (conversionFactorFreight == null && quoteFreightPrice) warnings.add("Missing 'UOM conversion' from Freight UOM (${freightRow?.FreightUOM}) to Product Master UOM (${unitOfMeasure}) for material ${material}")

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
                pricePerUOM = getFormattedPrice(scaleDeliveredPrice, quoteCurrencyDecimals, quotePer) + " / " + scalePriceUOM
                pricePerUOMPDFExport = getFormattedPrice(scaleDeliveredPrice, quoteCurrencyDecimals, quotePer) + " / " + priceUomPdfExport
            } else {
                effectiveDate = quote?.PriceValidFrom ? outputFormat.format(quote?.PriceValidFrom) : null
                pricePerUOM = getFormattedPrice(scalePrice, quoteCurrencyDecimals, quotePer) + " / " + scalePriceUOM
                pricePerUOMPDFExport = getFormattedPrice(scalePrice, quoteCurrencyDecimals, quotePer) + " / " + priceUomPdfExport
            }

            minQtyPerUOM = api.formatNumber("###,###.##", scaleQty) + " / " + moqUOM
            minQtyPerUOMPDFExport = api.formatNumber("###,###.##", scaleQty) + " / " + moqUomPdfExport

            standardPrice = scalePrice != null && conversionFactor != null ? getFormattedPrice(scalePrice * conversionFactor, quoteCurrencyDecimals, quotePer) : null
            standardDeliveredPrice = scaleDeliveredPrice != null && conversionFactor != null ? getFormattedPrice(scaleDeliveredPrice * conversionFactor, quoteCurrencyDecimals, quotePer) : null
            standardFreightPrice = quoteFreightPrice != null && conversionFactorFreight != null ? getFormattedPrice(quoteFreightPrice * conversionFactorFreight, quoteCurrencyDecimals) : null

            if (quote?.PriceType != "4") {
                exportPdf.add(
                        exportRow + [
                                "effectiveDate" : effectiveDate,
                                "moqUom"        : minQtyPerUOMPDFExport,
                                "priceUom"      : pricePerUOMPDFExport
                        ] as Map<String, Object>
                ) //TODO CHECK
            }
            summary.addRow(
                    row + [
                            (columnLabels."EffectiveDate")              : effectiveDate,
                            (columnLabels."MinQtyPerUOM")               : minQtyPerUOM,
                            (columnLabels."PricePerUOM")                : pricePerUOM,
                            (columnLabels."OriginPrice")                : getFormattedPrice(scalePrice, quoteCurrencyDecimals, quotePer),
                            (columnLabels."FreightPrice")               : quoteFreightPrice != null ? getFormattedPrice(quoteFreightPrice, quoteCurrencyDecimals) : "",
                            (columnLabels."DeliveredPrice")             : getFormattedPrice(scaleDeliveredPrice, quoteCurrencyDecimals, quotePer),
                            (columnLabels."OriginStandardUomPrice")     : standardPrice,
                            (columnLabels."FreightStandardUomPrice")    : standardFreightPrice,
                            (columnLabels."DeliveredStandardUomPrice")  : standardDeliveredPrice
                    ] as Map<String, Object>
            )
        }
    }
}
api.local.exportPdf = exportPdf

summary.setColumnFormat("SalesRepName", FieldFormatType.TEXT)
summary.setColumnFormat("ProductCodeDescription", FieldFormatType.TEXT)
summary.setColumnFormat("CustomerMaterialNumber", FieldFormatType.TEXT)
summary.setColumnFormat("Origin", FieldFormatType.TEXT)

summary.setColumnFormat("OriginPrice", FieldFormatType.MONEY_USD)
summary.setColumnFormat("FreightPrice", FieldFormatType.MONEY_USD)
summary.setColumnFormat("DeliveredPrice", FieldFormatType.MONEY_USD)
summary.setColumnFormat("OriginStandardUomPrice", FieldFormatType.MONEY_USD)
summary.setColumnFormat("FreightStandardUomPrice", FieldFormatType.MONEY_USD)
summary.setColumnFormat("DeliveredStandardUomPrice", FieldFormatType.MONEY_USD)

summary.setColumnFormat("DeliveredLocation", FieldFormatType.TEXT)
summary.setColumnFormat("ModeOfSales", FieldFormatType.TEXT)
summary.setColumnFormat("EffectiveDate", FieldFormatType.DATE)
summary.setColumnFormat("WPG", FieldFormatType.TEXT)
summary.setColumnFormat("MinQtyPerUOM", FieldFormatType.TEXT)
summary.setColumnFormat("PricePerUOM", FieldFormatType.TEXT)
summary.setColumnFormat("IncoTermsPerFreight", FieldFormatType.TEXT)
summary.setColumnFormat("IndexIndicator", FieldFormatType.TEXT)
summary.setColumnFormat("QuoteLastUpdate", FieldFormatType.DATETIME)

summary.withEnableClientFilter(true)

warnings.unique()
warnings.each {
    api.addWarning(it)
}

return summary

def findExclusionForLine(String soldToValue, String shipToValue, String ph1Value, String materialValue) {
    exclusion = null

    exclusionSoldTo = out.LoadExclusionCPT[soldToValue] ?: out.LoadExclusionCPT["*"]

    if (!exclusionSoldTo) return null
    exclusionShipToSpecific = exclusionSoldTo[shipToValue]
    if (exclusionShipToSpecific) {
        exclusion = getExclusionStartingFromExclusionShipTo(exclusionShipToSpecific, ph1Value, materialValue)
    }
    if (!exclusion) {
        exclusionShipToNotSpecific = exclusionSoldTo["*"]
        if (!exclusionShipToNotSpecific) return null
        exclusion = getExclusionStartingFromExclusionShipTo(exclusionShipToNotSpecific, ph1Value, materialValue)
    }

    return exclusion?.find()
}

def getExclusionStartingFromExclusionShipTo (exclusionShipTo, String ph1Value, String materialValue) {
    exclusion = null
    exclusionPH1Specific = exclusionShipTo[ph1Value]
    if (exclusionPH1Specific) {
        exclusion = exclusionPH1Specific[materialValue] ?: exclusionPH1Specific["*"]
    }
    if (!exclusion) {
        exclusionPH1NotSpecific = exclusionShipTo["*"]
        if (exclusionPH1NotSpecific) {
            exclusion = exclusionPH1NotSpecific[materialValue] ?: exclusionPH1NotSpecific["*"]
        }
    }

    return exclusion
}

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