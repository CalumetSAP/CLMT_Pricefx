import net.pricefx.common.api.FieldFormatType

import java.text.SimpleDateFormat

def visited = new HashSet<>()
def constants = libs.DashboardConstantsLibrary.PricePublishing

def plants = api.global.plant
def modeOfTransportations = api.global.modeOfTransportation
def customerMaster = api.global.customers
def products = api.global.products
def currencyDecimals = api.global.currencyDecimals
def uomDescription = api.global.uomDescription
def pricingData = api.global.pricingData
def salesData = out.LoadSalesData

def warnings = []

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
def outputFormat = new SimpleDateFormat("MM/dd/yyyy")
def inputFormatDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
def outputFormatDateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

String pricingDate = out.Filters?.get(constants.PRICING_DATE_INPUT_KEY)//"2025-02-10"
def showJobbers = out.Filters?.get(constants.SHOW_JOBBER_SRP_MAP_INPUT_KEY)

def columnLabels = [
        MaterialNumber                  : constants.MATERIAL_NUMBER,
        LegacyPartNumber                : constants.LEGACY_PART_NUMBER,
        CustomerMaterialNumber          : constants.CUSTOMER_MATERIAL_NUMBER,
        MaterialDescription             : constants.MATERIAL_DESCRIPTION,
        Price                           : constants.PRICE,
        PriceUOM                        : constants.PRICE_UOM,
        Origin                          : constants.ORIGIN,
        DeliveredLocation               : constants.DELIVERED_LOCATION,
        EffectiveDate                   : constants.EFFECTIVE_DATE,
        MOQPerUOM                       : constants.MOQ_PER_UOM,
        IncoTermsPerFreight             : constants.PB_INCOTERMS_PER_FREIGHT,
        Jobbers                         : constants.JOBBERS,
        SRP                             : constants.SRP,
        MAP                             : constants.MAP,
        SalesRepName                    : constants.SALES_REP_NAME,
        ModeOfSales                     : constants.MODE_OF_SALES,
        WPG                             : constants.WPG,
        PriceType                       : constants.PriceType,
        ContractNumber                  : constants.ContractNumber,
        QuoteId                         : constants.QuoteId,
        ContractItem                    : constants.ContractItem,
        SalesOrg                        : constants.SalesOrg,
        Division                        : constants.Division,
        Pricelist                       : constants.Pricelist,
        QuoteLastUpdate                 : constants.QuoteLastUpdate,
        "SoldTo"                        : constants.SOLD_TO,
        "SoldToName"                    : constants.SOLD_TO_NAME,
        "ShipTo"                        : constants.SHIP_TO,
        "ShipToName"                    : constants.SHIP_TO_NAME,
        "CustomerGroup"                 : constants.CUSTOMER_GROUP,
        "ShipToIndustry"                : constants.SHIP_TO_INDUSTRY,
        "ExpirationDate"                : constants.EXPIRATION_DATE,
        "NetWeightUOM"                  : constants.NET_WEIGHT_UOM,
        "Currency"                      : constants.CURRENCY,
        "StandardUom"                   : constants.STANDARD_UOM,
        "SalesRepNumber"                : constants.SALES_REP_NUMBER,
        "ShippingPointReceivingPt"      : constants.SHIPPPING_POINT_RECEIVING_PT,
        "ProductHierarchy"              : constants.PRODUCT_HIERARCHY,
        "ProductHierarchyLevel2"        : constants.PRODUCT_HIERARCHY_LEVEL_2,
        "SoldToIncludedInExclusionTable": constants.SOLD_TO_INCLUDED_IN_EXCLUSION_TABLE,
        "CommentFromExclusionTable"     : constants.COMMENT_FROM_EXCLUSION_TABLE,
        "FreightTerm"                   : constants.FREIGHT_TERM,
        "ShipToCity"                    : constants.SHIP_TO_CITY,
        "ShipToZip"                     : constants.SHIP_TO_ZIP,
        "ShipToCountry"                 : constants.SHIP_TO_COUNTRY,
        "ShipToState"                   : constants.SHIP_TO_STATE,
        "NamedPlace"                    : constants.NAMED_PLACE,
        "OriginPrice"                   : constants.ORIGIN_PRICE,
        "FreightPrice"                  : constants.FREIGHT_PRICE,
        "DeliveredPrice"                : constants.DELIVERED_PRICE,
        "OriginStandardUomPrice"        : constants.ORIGIN_STANDARD_UOM_PRICE,
        "FreightStandardUomPrice"       : constants.FREIGHT_STANDARD_UOM_PRICE,
        "DeliveredStandardUomPrice"     : constants.DELIVERED_STANDARD_UOM_PRICE,
        "FreightValidFrom"              : constants.FREIGHT_VALID_FROM,
        "FreightValidTo"                : constants.FREIGHT_VALID_TO,
]

def summaryRows = []

def exportPdf = []

def groupedQuotes = api.local.quotes as Map
def row, exportRow, material, legacyPart, customerMaterialNumber, materialDescription, price, priceUOM,
        origin, deliveredLocation, effectiveDate, moqPerUOM, incotermsPerFreight, incotermsPerFreightPDF, pricingItem, jobbers, srp, map,
        productPM, shipToCustomer, shipTo, shipToList, maxDate, salesOrg, pricelist, scales, firstScale, validScales, scaleToMOQConversionFactor,
        moq, moqUOM, conversionFactor, unitOfMeasure, scaleQty, scalePrice, quoteCurrencyDecimals, scalePriceUOM, per, moqUomPdfExport, moqPerUOMPDF, soldToName,
        brand, productHierarchy1, productHierarchy2, productHierarchy3, priceUomPdfExport, salesPersonName, salesPerson, modeOfSales, wpg, newQuoteLastUpdateDate,
        shipToName, customerGroup, shipToIndustry, shipToCity, shipToState, shipToZip, shipToCountry, netWeightUOM, productHierarchy, exclusion, quoteFreightPrice,
        standardFreightPrice, standardPrice, standardDeliveredPrice, originPrice
def zbplItem, zbplCRItem, zbplItems, zbplCRItems, possibleItems, possibleCRItems
def keys, quotes, firstQuote
for(value in groupedQuotes) {
    row = [:]
    exportRow = [:]
    keys = value.getKey()
    quotes = value.getValue()
    firstQuote = quotes?.find()

    shipToList = quotes?.collect { it.ShipTo }?.unique()
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

    material = keys?.get(0)
    productPM = products?.find{product -> product.sku == material}

    materialDescription = productPM?.label
    legacyPart = productPM?.attribute12?.toString()
    brand = productPM?.attribute2
    productHierarchy = productPM?.attribute1
    productHierarchy1 = productPM?.attribute14
    productHierarchy2 = productPM?.attribute16
    productHierarchy3 = productPM?.attribute18

    unitOfMeasure = productPM?.unitOfMeasure
    customerMaterialNumber = [firstQuote?.CustomerMaterial, firstQuote?.ThirdPartyCustomer]?.findAll { it != null }?.join(" / ")
    soldToName = customerMaster.find { customer -> customer.customerId == firstQuote.SoldTo}?.name
    per = firstQuote?.Per
    price = firstQuote?.DeliveredPrice
    priceUOM = firstQuote?.PricingUOM
    origin = plants?.get(firstQuote.Plant)
    incotermsPerFreight = firstQuote?.Incoterm + " / " + firstQuote?.FreightTermValue
    incotermsPerFreightPDF = firstQuote?.Incoterm + " / \n" + firstQuote?.FreightTermValue
    moq = firstQuote?.MOQ
    moqUOM = firstQuote?.MOQUOM
    moqPerUOM = moq + " / " + moqUOM

    salesOrg = firstQuote?.SalesOrg
    pricelist = firstQuote?.PriceListPLT
    quoteCurrencyDecimals = currencyDecimals?.get(firstQuote?.Currency)

    pricingItem = pricingData?.get(material + "|" + pricelist)
    jobbers = libs.QuoteLibrary.RoundingUtils.round(pricingItem?.JobberPrice?.toBigDecimal(), (quoteCurrencyDecimals ?: 2))
    srp = libs.QuoteLibrary.RoundingUtils.round(pricingItem?.SRP?.toBigDecimal(), (quoteCurrencyDecimals ?: 2))
    map = libs.QuoteLibrary.RoundingUtils.round(pricingItem?.MAP?.toBigDecimal(), (quoteCurrencyDecimals ?: 2))

    salesPerson = customerMaster.find{customer -> customer.customerId == firstQuote.SalesPerson}
    salesPersonName = salesPerson?.name
    modeOfSales = modeOfTransportations?.get(firstQuote.ModeOfTransportation)
    wpg = productPM?.attribute4
    newQuoteLastUpdateDate = firstQuote?.QuoteLastUpdate ? inputFormatDateTime.parse(firstQuote?.QuoteLastUpdate?.toString()) : null
    shipToName = shipToCustomer?.name
    customerGroup = salesData?.find { it.customerId == firstQuote?.SoldTo }?.attribute6
    shipToIndustry = shipToCustomer?.attribute12
    shipToCity = shipToCustomer?.attribute5
    shipToState = shipToCustomer?.attribute7
    shipToZip = shipToCustomer?.attribute6
    shipToCountry = shipToCustomer?.attribute4
    netWeightUOM = productPM?.attribute5
    exclusion = findExclusionForLine(firstQuote?.SoldTo as String, firstQuote?.ShipTo as String, productHierarchy1 as String, material as String)

    quoteFreightPrice = firstQuote?.DeliveredPrice != null && firstQuote?.Price != null ? firstQuote?.DeliveredPrice - firstQuote?.Price : BigDecimal.ZERO

    (row[columnLabels."MaterialNumber"] = material)
    (row[columnLabels."LegacyPartNumber"] = legacyPart)
    (row[columnLabels."CustomerMaterialNumber"] = customerMaterialNumber)
    (row[columnLabels."MaterialDescription"] = materialDescription)
    (row[columnLabels."Price"] = price)
    (row[columnLabels."PriceUOM"] = priceUOM)
    (row[columnLabels."Origin"] = origin)
    (row[columnLabels."DeliveredLocation"] = deliveredLocation)
    (row[columnLabels."MOQPerUOM"] = moqPerUOM)
    (row[columnLabels."IncoTermsPerFreight"] = incotermsPerFreight)
    if (showJobbers) (row[columnLabels."Jobbers"] = formatPrice(jobbers))
    if (showJobbers) (row[columnLabels."SRP"] = formatPrice(srp))
    if (showJobbers) (row[columnLabels."MAP"] = formatPrice(map))
    (row[columnLabels."SalesRepName"] = salesPersonName)
    (row[columnLabels."ModeOfSales"] = modeOfSales)
    (row[columnLabels."WPG"] = wpg)
    (row[columnLabels."PriceType"] = firstQuote?.PriceType)
    (row[columnLabels."QuoteId"] = firstQuote?.QuoteID)
    (row[columnLabels."ContractNumber"] = firstQuote?.SAPContractNumber)
    (row[columnLabels."Pricelist"] = pricelist)
    (row[columnLabels."ContractItem"] = firstQuote?.SAPLineID)
    (row[columnLabels."SalesOrg"] = salesOrg)
    (row[columnLabels."Division"] = firstQuote?.Division)
    (row[columnLabels."QuoteLastUpdate"] = newQuoteLastUpdateDate ? outputFormatDateTime.format(newQuoteLastUpdateDate) : null)
    (row[columnLabels."SoldTo"] = firstQuote?.SoldTo)
    (row[columnLabels."SoldToName"] = soldToName)
    (row[columnLabels."ShipTo"] = firstQuote?.ShipTo)
    (row[columnLabels."ShipToName"] = shipToName)
    (row[columnLabels."CustomerGroup"] = customerGroup)
    (row[columnLabels."ShipToIndustry"] = shipToIndustry)
    (row[columnLabels."ExpirationDate"] = firstQuote?.PriceValidTo ? outputFormat.format(firstQuote?.PriceValidTo) : null)
    (row[columnLabels."NetWeightUOM"] = netWeightUOM)
    (row[columnLabels."Currency"] = firstQuote?.Currency)
    (row[columnLabels."StandardUom"] = unitOfMeasure)
    (row[columnLabels."SalesRepNumber"] = firstQuote?.SalesPerson)
    (row[columnLabels."ShippingPointReceivingPt"] = firstQuote?.ShippingPoint)
    (row[columnLabels."ProductHierarchy"] = productHierarchy)
    (row[columnLabels."ProductHierarchyLevel2"] = productHierarchy2)
    (row[columnLabels."SoldToIncludedInExclusionTable"] = out.LoadExclusionCPT?.get(firstQuote?.SoldTo?.toString()) ? "X" : "")
    (row[columnLabels."CommentFromExclusionTable"] = exclusion?.attribute6)
    (row[columnLabels."FreightTerm"] = firstQuote?.FreightTerm)
    (row[columnLabels."ShipToCity"] = shipToCity)
    (row[columnLabels."ShipToZip"] = shipToZip)
    (row[columnLabels."ShipToState"] = shipToState)
    (row[columnLabels."ShipToCountry"] = shipToCountry)
    (row[columnLabels."NamedPlace"] = firstQuote?.NamedPlace)
    (row[columnLabels."FreightValidFrom"] = firstQuote?.FreightValidFrom)
    (row[columnLabels."FreightValidTo"] = firstQuote?.FreightValidTo)

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
                        it.validFrom <= sdf.parse(pricingDate) &&
                        it.validTo >= sdf.parse(pricingDate)
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
                if (scaleToMOQConversionFactor == null) api.throwException("Missing conversion from ZBPL CR Scale UOM (${zbplCRItem.attribute3}) to MOQ UOM (${moqUOM}) for material ${material}")

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
                it.ValidFrom <= sdf.parse(pricingDate) &&
                        it.ValidTo >= sdf.parse(pricingDate)
            }
            zbplItem = possibleItems?.max { it.lastUpdateDate }
            if (!zbplItem) continue

            effectiveDate = zbplItem?.ValidFrom ? outputFormat.format(zbplItem?.ValidFrom) : null

            price = zbplItem?.Amount
            priceUOM = zbplItem?.UnitOfMeasure
            priceUomPdfExport = uomDescription?.get(priceUOM) ? uomDescription?.get(priceUOM) : priceUOM

            scales = api.global.ZBPLScales.get(zbplItem?.ConditionRecordNo)
            if (scales) {
                //TODO ask if ScaleUoM will be filled, what to do when is null?
                scaleToMOQConversionFactor = zbplItem?.ScaleUoM ? libs.QuoteLibrary.Conversion.getConversionFactor(material, zbplItem?.ScaleUoM, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal() : 1
                if (scaleToMOQConversionFactor == null) api.throwException("Missing conversion from ZBPL Scale UOM (${zbplItem?.ScaleUoM}) to MOQ UOM (${moqUOM}) for material ${material}")

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

        conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, priceUOM, unitOfMeasure, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
        if (conversionFactor == null) warnings.add("Missing 'UOM conversion' from Pricing UOM (${priceUOM}) to Product Master UOM (${unitOfMeasure}) for material ${material}")

        for (validScale in validScales) {
            scaleQty = validScale.ScaleQty
            scalePrice = per && validScale.Price ? validScale.Price / per : validScale.Price

            price = formatPrice(scalePrice?.toBigDecimal())

            moqPerUOM = api.formatNumber("###,###.##", scaleQty) + " / " + moqUOM
            moqPerUOMPDF = api.formatNumber("###,###.##", scaleQty) + " / " + moqUomPdfExport

            originPrice = scalePrice != null && quoteFreightPrice != null ? scalePrice - quoteFreightPrice : scalePrice

            standardPrice = originPrice != null && conversionFactor != null ? formatPrice(originPrice * conversionFactor) : null
            standardFreightPrice = quoteFreightPrice != null && conversionFactor != null ? formatPrice(quoteFreightPrice?.toBigDecimal() * conversionFactor) : null
            standardDeliveredPrice = scalePrice != null && conversionFactor != null ? formatPrice(scalePrice?.toBigDecimal() * conversionFactor) : null

            exportPdf.add(
                    exportRow + [
                            "effectiveDate" : effectiveDate,
                            "moq"           : scaleQty,
                            "moqUom"        : moqPerUOMPDF,
                            "price"         : price ?: "",
                            "priceUOM"      : priceUomPdfExport ?: ""
                    ] as Map<String, Object>
            )

            summaryRows.add(
                    row + [
                            (columnLabels."EffectiveDate")            : effectiveDate,
                            (columnLabels."Price")                    : price,
                            (columnLabels."PriceUOM")                 : priceUOM,
                            (columnLabels."MOQPerUOM")                : moqPerUOM,
                            (columnLabels."OriginPrice")              : formatPrice(originPrice?.toBigDecimal()),
                            (columnLabels."FreightPrice")             : formatPrice(quoteFreightPrice),
                            (columnLabels."DeliveredPrice")           : price,
                            (columnLabels."OriginStandardUomPrice")   : standardPrice,
                            (columnLabels."FreightStandardUomPrice")  : standardFreightPrice,
                            (columnLabels."DeliveredStandardUomPrice"): standardDeliveredPrice
                    ] as Map<String, Object>
            )
        }
    } else {
        priceUOM = firstQuote?.PricingUOM
        priceUomPdfExport = uomDescription?.get(priceUOM) ? uomDescription?.get(priceUOM) : priceUOM
        scales = api.local.quoteScales.get(firstQuote?.LineID)

        if (scales) {
            scaleToMOQConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, scales.find().ScaleUOM, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
            if (scaleToMOQConversionFactor == null) api.throwException("Missing conversion from Scale UOM (${scales.find().ScaleUOM}) to MOQ UOM (${moqUOM}) for material ${material}")

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
        conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, validScales.find().PriceUOM, unitOfMeasure, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal()
        if (conversionFactor == null) warnings.add("Missing 'UOM conversion' from Pricing UOM (${priceUOM}) to Product Master UOM (${unitOfMeasure}) for material ${material}")

        maxDate = getEffectiveDate(pricingDate, quotes?.collect { it.PriceValidFrom})
        for (validScale in validScales) {
            scaleQty = validScale.ScaleQty
            scalePrice = per && validScale.Price ? validScale.Price / per : validScale.Price
            scalePriceUOM = validScale.PriceUOM

            effectiveDate = maxDate ? outputFormat.format(maxDate) : null
            price = formatPrice(scalePrice?.toBigDecimal())
            priceUOM = scalePriceUOM
            priceUomPdfExport = uomDescription?.get(priceUOM) ? uomDescription?.get(priceUOM) : priceUOM

            moqPerUOM = api.formatNumber("###,###.##", scaleQty) + " / " + moqUOM
            moqPerUOMPDF = api.formatNumber("###,###.##", scaleQty) + " / " + moqUomPdfExport

            originPrice = scalePrice != null && quoteFreightPrice != null ? scalePrice - quoteFreightPrice : scalePrice

            standardPrice = originPrice != null && conversionFactor != null ? formatPrice(originPrice * conversionFactor) : null
            standardFreightPrice = quoteFreightPrice != null && conversionFactor != null ? formatPrice(quoteFreightPrice?.toBigDecimal() * conversionFactor) : null
            standardDeliveredPrice = scalePrice != null && conversionFactor != null ? formatPrice(scalePrice?.toBigDecimal() * conversionFactor) : null

            if (firstQuote?.PriceType != "4") {
                exportPdf.add(
                        exportRow + [
                                "effectiveDate" : effectiveDate,
                                "moq"           : scaleQty,
                                "moqUom"        : moqPerUOMPDF,
                                "price"         : price ?: "",
                                "priceUOM"      : priceUomPdfExport ?: ""
                        ] as Map<String, Object>
                )
            }
            summaryRows.add(
                    row + [
                            (columnLabels."EffectiveDate")            : effectiveDate,
                            (columnLabels."Price")                    : price,
                            (columnLabels."PriceUOM")                 : priceUOM,
                            (columnLabels."MOQPerUOM")                : moqPerUOM,
                            (columnLabels."OriginPrice")              : formatPrice(originPrice?.toBigDecimal()),
                            (columnLabels."FreightPrice")             : formatPrice(quoteFreightPrice),
                            (columnLabels."DeliveredPrice")           : price,
                            (columnLabels."OriginStandardUomPrice")   : standardPrice,
                            (columnLabels."FreightStandardUomPrice")  : standardFreightPrice,
                            (columnLabels."DeliveredStandardUomPrice"): standardDeliveredPrice
                    ] as Map<String, Object>
            )
        }
    }
}
api.local.exportPdf = exportPdf

def hasLegacyPartNo = summaryRows?.find { it[(columnLabels."LegacyPartNumber")] }
def hasJobbbers = summaryRows?.find { it[(columnLabels."Jobbers")] }
def hasSRP = summaryRows?.find { it[(columnLabels."SRP")] }
def hasMAP = summaryRows?.find { it[(columnLabels."MAP")] }

if (!hasLegacyPartNo) columnLabels.remove("LegacyPartNumber")
if (!hasJobbbers) columnLabels.remove("Jobbers")
if (!hasSRP) columnLabels.remove("SRP")
if (!hasMAP) columnLabels.remove("MAP")

def summary = api.newMatrix(columnLabels.collect({ column -> column.value }))

def rowToAdd
summaryRows.each {
    rowToAdd = it

    if (!hasLegacyPartNo) rowToAdd.remove(constants.LEGACY_PART_NUMBER)
    if (!hasJobbbers) rowToAdd.remove(constants.JOBBERS)
    if (!hasSRP) rowToAdd.remove(constants.SRP)
    if (!hasMAP) rowToAdd.remove(constants.MAP)

    summary.addRow(rowToAdd)
}

summary.setColumnFormat("MaterialNumber", FieldFormatType.TEXT)
summary.setColumnFormat("CustomerMaterialNumber", FieldFormatType.TEXT)
summary.setColumnFormat("MaterialDescription", FieldFormatType.TEXT)
summary.setColumnFormat("Price", FieldFormatType.NUMERIC_LONG)
summary.setColumnFormat("PriceUOM", FieldFormatType.TEXT)
summary.setColumnFormat("Origin", FieldFormatType.TEXT)
summary.setColumnFormat("DeliveredLocation", FieldFormatType.TEXT)
summary.setColumnFormat("EffectiveDate", FieldFormatType.DATE)
summary.setColumnFormat("MOQPerUOM", FieldFormatType.TEXT)
summary.setColumnFormat("IncoTermsPerFreight", FieldFormatType.TEXT)
if (!hasLegacyPartNo) summary.setColumnFormat("LegacyPartNumber", FieldFormatType.TEXT)
if (!hasJobbbers) summary.setColumnFormat("Jobbers", FieldFormatType.TEXT)
if (!hasSRP) summary.setColumnFormat("SRP", FieldFormatType.TEXT)
if (!hasMAP) summary.setColumnFormat("MAP", FieldFormatType.TEXT)

summary.withEnableClientFilter(true)

warnings.unique()
warnings.each {
    api.addWarning(it)
}

api.local.hasJobbbers = hasJobbbers
api.local.hasSRP = hasSRP
api.local.hasMAP = hasMAP

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

String formatPrice(BigDecimal price) {
    if (!price) return price
    def parts = String.format('%.2f', price).split('\\.')
    def integerPart = parts[0].reverse().replaceAll(/(\d{3})(?=\d)/, '$1,').reverse()
    return "\$ ${integerPart}.${parts[1]}"
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