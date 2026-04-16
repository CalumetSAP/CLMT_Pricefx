import net.pricefx.common.api.InputType

import java.text.SimpleDateFormat

if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

def sdf = new SimpleDateFormat("yyyy-MM-dd")

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final dateUtils = libs.QuoteLibrary.DateUtils
final general = libs.QuoteConstantsLibrary.General
final calculations = libs.QuoteLibrary.Calculations

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = headerConfigurator?.get(headerConstants.DIVISION_ID)
def salesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)
def soldToOnly = headerConfigurator?.get(headerConstants.SOLD_TO_ONLY_QUOTE_ID)

def soldToIndustry = null
def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]
def incotermOptions = out.FindIncoTerm && !api.isInputGenerationExecution() ? out.FindIncoTerm as Map : [:]
def freightTermOptions = out.FindDropdownOptions ? out.FindDropdownOptions["FreightTerm"] as Map : [:]
def meansOfTransportationOptions = out.FindMeansOfTransportation && !api.isInputGenerationExecution() ? out.FindMeansOfTransportation as Map : [:]
def modeOfTransportationOptions = out.FindModeOfTransportation && !api.isInputGenerationExecution() ? out.FindModeOfTransportation as Map : [:]
def pricelistOptions = out.FindPricelist && !api.isInputGenerationExecution() ? out.FindPricelist as Map : [:]
def pricelistMap = out.FindPricelistOptions && !api.isInputGenerationExecution() ? out.FindPricelistOptions as Map : [:]
def productMasterData = out.FindProductMasterData && !api.isInputGenerationExecution() ? out.FindProductMasterData as Map : [:]
def customerMasterData = out.FindCustomerShipTo && !api.isInputGenerationExecution() ? out.FindCustomerShipTo as Map : [:]
def shippingPointNames = out.FindShippingPoint && !api.isInputGenerationExecution() ? out.FindShippingPoint as Map : [:]
def salesPersonOptions = api.local.salesPersonTable && !api.isInputGenerationExecution() ? api.local.salesPersonTable as List : []
def globalUOMConversionTable = out.FindGlobalUOMConversionTable && !api.isInputGenerationExecution() ? out.FindGlobalUOMConversionTable as Map : [:]
def uomConversionTable = out.FindUOMConversionTable && !api.isInputGenerationExecution() ? out.FindUOMConversionTable as Map : [:]
//def costPX = out.FindCostPX && !api.isInputGenerationExecution() ? out.FindCostPX as Map : [:]
//def guardrails = api.local.guardrailsTable && !api.isInputGenerationExecution() ? api.local.guardrailsTable as Map : [:]
def packageDifferential = out.FindPackageDifferential && !api.isInputGenerationExecution() ? out.FindPackageDifferential as Map : [:]
def approversMap = out.FindApprovers && !api.isInputGenerationExecution() ? out.FindApprovers as Map : [:]
def exclusions = out.FindExclusions && !api.isInputGenerationExecution() ? out.FindExclusions as Map : [:]

def uomOptions = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions as Map : [:]

def headerEffectiveDate = headerConfigurator?.get(headerConstants.CONTRACT_EFFECTIVE_DATE_ID)
def defaultValidFromDate = headerEffectiveDate ? dateUtils.parseToDate(headerEffectiveDate) : dateUtils.getToday()

def days = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceValidTo"]?.values()?.find() as Integer : null
def defaultValidToDate = dateUtils.sumDays(defaultValidFromDate, days)

def creator = quoteProcessor?.getQuoteView()?.createdByName
def salesCreator = creator && api.isUserInGroup(general.USER_GROUP_SALES, creator) ? creator : null

def dateOptions = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                   "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
                   "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]

def params = [
        Division                    : division,
        SalesOrg                    : salesOrg,
        IsSoldToOnly                : soldToOnly,
        SoldToIndustry              : soldToIndustry,
        DropdownOptions             : dropdownOptions,
        IncotermOptions             : incotermOptions,
        MeansOfTransportationOptions: meansOfTransportationOptions,
        ModeOfTransportationOptions : modeOfTransportationOptions,
        Pricelists                  : pricelistOptions,
        ShipTo                      : customerMasterData,
        ValidFromDate               : defaultValidFromDate,
        ValidToDate                 : defaultValidToDate,
        ShippingPointNames          : shippingPointNames,
        SalesPerson                 : salesPersonOptions,
        GlobalUOMTable              : globalUOMConversionTable,
//        UOMTable                    : uomConversionTable,
//        CostPX                      : costPX,
//        Guardrails                  : guardrails,
        PackageDifferential         : packageDifferential,
        SalesCreator                : salesCreator,
        Exclusions                  : exclusions
]

def plantOptions, plant, shippingPointOptions, meansOfTransportation, modeOfTransportation, shippingPoint, priceType
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder || api.local.removedLineIds?.contains(lnProduct.lineId)) continue
    String lineId = lnProduct.lineId as String

//    def uoms = ["Flat Rate"]
    def uoms = []
    def uomsPerMaterial = uomOptions?.getOrDefault(lnProduct.sku, [])
    uomsPerMaterial?.add("KG")
    uomsPerMaterial?.sort()
    uomsPerMaterial?.unique()
    uoms.addAll(uomsPerMaterial)

    plant = calculations.getInputValue(lnProduct, lineItemConstants.PLANT_ID)

    shippingPointOptions = findShippingPoint(shippingPointNames, plant)

    // Update Option Inputs
    updateOptionInput(lineId, lineItemConstants.SHIPPING_POINT_ID, shippingPointOptions)
    updateOptionInput(lineId, lineItemConstants.CURRENCY_ID, dropdownOptions["Currency"]?.values())
    updateOptionInput(lineId, lineItemConstants.MOQ_UOM_ID, uomsPerMaterial)
    updateOptionInput(lineId, lineItemConstants.MEANS_OF_TRANSPORTATION_ID, meansOfTransportationOptions?.values())
    updateOptionInput(lineId, lineItemConstants.MODE_OF_TRANSPORTATION_ID, modeOfTransportationOptions?.values())
    updateOptionInput(lineId, lineItemConstants.PRICE_TYPE_ID, dropdownOptions["PriceType"]?.values())
    def pricelistOptionsMap = pricelistMap?.get(lnProduct.sku)?.toList()?.collectEntries { item ->
        def key = item.split(" - ")[0]
        [(key): item]
    } ?: [:]
    updateMapOptionInput(lineId, lineItemConstants.PRICE_LIST_ID, pricelistOptionsMap)
    updateOptionInput(lineId, lineItemConstants.PRICING_UOM_ID, uomsPerMaterial)
    updateMapOptionInput(lineId, lineItemConstants.NUMBER_OF_DECIMALS_ID, dropdownOptions["NumberOfDecimals"] as Map)
    updateMapOptionInput(lineId, lineItemConstants.INCO_TERM_ID, incotermOptions)
    updateOptionInput(lineId, lineItemConstants.FREIGHT_TERM_ID, freightTermOptions?.values())
    updateOptionInput(lineId, lineItemConstants.SALES_PERSON_ID, salesPersonOptions)
    updateOptionInput(lineId, lineItemConstants.REJECTION_REASON_ID, dropdownOptions["RejectionReason"]?.values())

//    updateOptionInput(lineId, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, dropdownOptions["RejectionReason"]?.values())
    updateOptionInput(lineId, lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID, dropdownOptions["ReferencePeriod"]?.values())
    updateOptionInput(lineId, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID, uomsPerMaterial)
    updateOptionInput(lineId, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID, dateOptions)
    updateMapOptionInput(lineId, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID, dropdownOptions["RecalculationPeriod"] as Map)
}

// Import line items
if (!api.local.addedContracts) return

def contracts = out.FindContractDSData
def freightValues = out.FindInitFreightValues
def recommendedValuesMap = out.CalculateInitRecommendedPrice ?: [:]
def referencePeriod, salesPerson, freightTerm, freightPreviousValues, numberOfDecimals, freightAmount, freightUOM, conversionFactor, newDeliveredPrice, recommendedValues
contracts?.each { contractNumber, lines ->
    lines?.each { line ->
        def parameters = []

//    def uoms = ["Flat Rate"]
        def uoms = []
        def uomsPerMaterial = uomOptions?.getOrDefault(line?.Material, [])
        uomsPerMaterial?.add("KG")
        uomsPerMaterial?.sort()
        uomsPerMaterial?.unique()
        uoms.addAll(uomsPerMaterial)

        plantOptions = out.FindPlants && !api.isInputGenerationExecution() ? out.FindPlants?.getOrDefault(line?.Material, [])?.sort() as List : []
        plant = api.local.plantNames?.getOrDefault(line.Plant, line.Plant)

        shippingPointOptions = findShippingPoint(shippingPointNames, plant)
        shippingPoint = line.ShippingPoint ? shippingPointOptions?.find { it?.toString()?.startsWith(line.ShippingPoint) } : line.ShippingPoint

        meansOfTransportation = meansOfTransportationOptions?.get(line.MeansOfTransportation) ?: line.MeansOfTransportation
        modeOfTransportation = meansOfTransportationOptions?.get(line.ModeOfTransportation) ?: line.ModeOfTransportation

        salesPerson = line?.SalesPerson ? (salesPersonOptions?.find { it.toString().startsWith(line?.SalesPerson) } ?: line?.SalesPerson) : line?.SalesPerson

        referencePeriod = line?.ReferencePeriod ? dropdownOptions["ReferencePeriod"]?.get(line?.ReferencePeriod) : null
        freightTerm = line?.FreightTerm ? dropdownOptions["FreightTerm"]?.get(line?.FreightTerm) : null

        numberOfDecimals = line?.NumberofDecimals
        if (!numberOfDecimals && line?.Currency == "EUR") numberOfDecimals = "2"

        recommendedValues = recommendedValuesMap?.get(contractNumber + "|" + line?.LineNumber)
        def dsData = [
                SAPContractNumber      : contractNumber,
                SAPLineId              : line?.LineNumber,
                QuoteID                : line?.QuoteID,
                LineId                 : line?.LineID,
                Material               : line?.Material,
                Description            : line?.Description,
                SoldTo                 : line?.SoldTo,
                ShipTo                 : line?.ShipTo,
                Plant                  : plant,
                IncoTerm               : line?.Incoterm,
                FreightTerm            : freightTerm ? line?.FreightTerm : null,
                NamedPlace             : line?.NamedPlace,
                ShippingPoint          : shippingPoint,
                Price                  : line?.Price,
                DeliveredPrice         : line?.DeliveredPrice,
                PricingUOM             : line?.PricingUOM,
                Currency               : line?.Currency,
                NumberofDecimals       : numberOfDecimals,
                CompetitorPrice        : line?.CompetitorPrice,
                MOQ                    : line?.MOQ,
                PriceValidFrom         : line?.PriceValidFrom,
                PriceValidTo           : line?.PriceValidTo,
                SalesPerson            : salesPerson,
                CustomerMaterial       : line?.CustomerMaterial,
                ThirdPartyCustomer     : line?.ThirdPartyCustomer,
                MeansOfTransportation  : line?.MeansOfTransportation,
                ModeOfTransportation   : line?.ModeOfTransportation,
                PriceListPLT           : line?.PriceListPLT,
                ApprovalSequence       : line?.ApprovalSequence,
                DiscountApprover       : line?.DiscountApprover,
                Cost                   : line?.Cost,
                RecommendedPrice       : recommendedValues?.RecommendedPrice ? libs.QuoteLibrary.RoundingUtils.round(recommendedValues?.RecommendedPrice?.toBigDecimal(), (numberOfDecimals?.toInteger() ?: 2.toInteger()))?.toString() : null,
                MaterialPackageStyle   : line?.MaterialPackageStyle,
                MOQUOM                 : line?.MOQUOM,
                Per                    : line?.Per,
                PriceType              : line?.PriceType,
                IndexNumberOne         : line?.IndexNumberOne,
                IndexNumberTwo         : line?.IndexNumberTwo,
                IndexNumberThree       : line?.IndexNumberThree,
                IndexNumberOnePercent  : line?.IndexNumberOnePercent,
                IndexNumberTwoPercent  : line?.IndexNumberTwoPercent,
                IndexNumberThreePercent: line?.IndexNumberThreePercent,
                ReferencePeriod        : line?.ReferencePeriod,
                Adder                  : line?.Adder,
                AdderUOM               : line?.AdderUOM,
                AdderNumberofDecimals  : line?.AdderNumberofDecimals,
                RecalculationDate      : line?.RecalculationDate,
                RecalculationPeriod    : line?.RecalculationPeriod,
                ReferencePeriodValue   : referencePeriod,
                FormulaApprover        : line?.FormulaApprover,
                IndexIndicator         : line?.IndexIndicator,
                Division               : line?.Division,
                SalesOrg               : line?.SalesOrg,
                ShipToName             : line?.ShipToName,
                FreightEstimate        : line?.FreightEstimate,
                FreightAmount          : line?.FreightAmount,
                FreightUOM             : line?.FreightUOM,
                FreightValidto         : line?.FreightValidto,
                FreightValidFrom       : line?.FreightValidFrom,
                RejectionReason        : line?.RejectionReason
        ]

        parameters.add(createParameter(lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID, dsData))

        meansOfTransportation = meansOfTransportationOptions?.get(line.MeansOfTransportation) ?: line.MeansOfTransportation
        modeOfTransportation = modeOfTransportationOptions?.get(line.ModeOfTransportation) ?: line.ModeOfTransportation
        priceType = dropdownOptions["PriceType"]?.get(line.PriceType) ?: line.PriceType

        freightPreviousValues = freightValues?.get(contractNumber + "|" + line?.LineNumber)

        parameters.add(createParameter(lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID, dsData.CustomerMaterial))
        parameters.add(createParameter(lineItemConstants.THIRD_PARTY_CUSTOMER_ID, dsData.ThirdPartyCustomer))
        parameters.add(createParameter(lineItemConstants.SHIPPING_POINT_ID, dsData.ShippingPoint, shippingPointOptions))
        parameters.add(createParameter(lineItemConstants.MOQ_ID, dsData.MOQ))
        parameters.add(createParameter(lineItemConstants.PREVIOUS_MOQ_ID, dsData.MOQ))
        parameters.add(createParameter(lineItemConstants.MOQ_UOM_ID, dsData.MOQUOM, uomsPerMaterial))
        parameters.add(createParameter(lineItemConstants.MEANS_OF_TRANSPORTATION_ID, meansOfTransportation, meansOfTransportationOptions?.values()))
        parameters.add(createParameter(lineItemConstants.MODE_OF_TRANSPORTATION_ID, modeOfTransportation, modeOfTransportationOptions?.values()))
        parameters.add(createParameter(lineItemConstants.PRICE_TYPE_ID, priceType, dropdownOptions["PriceType"]?.values()))
        parameters.add(createParameter(lineItemConstants.PREVIOUS_PRICE_TYPE_ID, line.PriceType))
        def pricelistOptionsMap = pricelistMap?.get(line?.Material)?.toList()?.collectEntries { item ->
            def key = item.split(" - ")[0]
            [(key): item]
        } ?: [:]
        parameters.add(createParameterWithMap(lineItemConstants.PRICE_LIST_ID, dsData.PriceListPLT, pricelistOptionsMap))
        parameters.add(createParameter(lineItemConstants.PREVIOUS_PRICELIST_ID, dsData.PriceListPLT))
        parameters.add(createParameter(lineItemConstants.COMPETITOR_PRICE_ID, dsData.CompetitorPrice))
        parameters.add(createParameter(lineItemConstants.PRICE_ID, dsData.Price))
        parameters.add(createParameter(lineItemConstants.PRICE_ID + "Previous", dsData.Price))

        freightAmount = freightPreviousValues?.FreightAmount?.toBigDecimal()
        freightUOM = freightPreviousValues?.FreightUOM
        if (freightAmount && freightUOM) {
            conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(line?.Material, freightUOM, dsData.PricingUOM, uomConversionTable, globalUOMConversionTable)?.toBigDecimal()
            if (conversionFactor == null) {
                freightAmount = null
            } else {
                freightAmount = freightAmount * conversionFactor
            }
        }

        newDeliveredPrice = dsData.Price != null && freightAmount != null ? libs.QuoteLibrary.RoundingUtils.round(dsData.Price?.toBigDecimal() + freightAmount, dsData.NumberofDecimals?.toInteger()) : dsData.Price

        parameters.add(createParameter(lineItemConstants.DELIVERED_PRICE_ID, newDeliveredPrice))
        parameters.add(createParameter(lineItemConstants.DELIVERED_PRICE_ID + "Previous", newDeliveredPrice))
        parameters.add(createParameter(lineItemConstants.PRICING_UOM_ID, dsData.PricingUOM, uomsPerMaterial))
        parameters.add(createParameterWithMap(lineItemConstants.NUMBER_OF_DECIMALS_ID, dsData.NumberofDecimals, dropdownOptions["NumberOfDecimals"] as Map))
        parameters.add(createParameter(lineItemConstants.PER_ID, dsData.Per))
        parameters.add(createParameter(lineItemConstants.CURRENCY_ID, dsData.Currency, dropdownOptions["Currency"]?.values()))
        parameters.add(createParameter(lineItemConstants.PRICE_VALID_FROM_ID, dsData.PriceValidFrom))
        parameters.add(createParameter(lineItemConstants.PRICE_VALID_TO_ID, dsData.PriceValidTo))
        parameters.add(createParameterWithMap(lineItemConstants.INCO_TERM_ID, dsData.IncoTerm, incotermOptions))
        parameters.add(createParameter(lineItemConstants.FREIGHT_TERM_ID, freightTerm, freightTermOptions?.values()))
        parameters.add(createParameter(lineItemConstants.NAMED_PLACE_ID, dsData.NamedPlace))
        parameters.add(createParameter(lineItemConstants.SALES_PERSON_ID, dsData.SalesPerson, salesPersonOptions))
        def rejectionReason = line?.RejectionReason ? dropdownOptions["RejectionReason"]?.get(line?.RejectionReason) : null
        parameters.add(createParameter(lineItemConstants.REJECTION_REASON_ID, rejectionReason, dropdownOptions["RejectionReason"]?.values()))
        parameters.add(createParameter(lineItemConstants.FREIGHT_ESTIMATE_ID, dsData.FreightEstimate))
        parameters.add(createParameter(lineItemConstants.FREIGHT_AMOUNT_ID, freightPreviousValues?.FreightAmount?.toBigDecimal()))
        parameters.add(createParameter(lineItemConstants.FREIGHT_VALID_FROM_ID, freightPreviousValues?.FreightValidFrom))
        parameters.add(createParameter(lineItemConstants.FREIGHT_VALID_TO_ID, freightPreviousValues?.FreightValidTo))
        parameters.add(createParameter(lineItemConstants.FREIGHT_UOM_ID, freightPreviousValues?.FreightUOM))
        parameters.add(createParameter(lineItemConstants.FREIGHT_PREVIOUS_VALUES_ID, [
                FreightAmount   : freightPreviousValues?.FreightAmount?.toBigDecimal(),
                FreightValidFrom: freightPreviousValues?.FreightValidFrom,
                FreightValidTo  : freightPreviousValues?.FreightValidTo,
                FreightUOM      : freightPreviousValues?.FreightUOM,
        ]))

        def indexList = []
        if (dsData.IndexNumberOne) indexList.add(dsData.IndexNumberOne)
        if (dsData.IndexNumberTwo) indexList.add(dsData.IndexNumberTwo)
        if (dsData.IndexNumberThree) indexList.add(dsData.IndexNumberThree)

        def isRejectedLine = rejectionReason != null
        parameters.add(createParameter(lineItemConstants.LINE_IS_REJECTED_ID, isRejectedLine))

        parameters.add(createParameter(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, indexList, []))
        parameters.add(createParameter(lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID, referencePeriod, dropdownOptions["ReferencePeriod"]?.values()))
        parameters.add(createParameter(lineItemConstants.PF_CONFIGURATOR_ADDER_ID, dsData.Adder))
        parameters.add(createParameter(lineItemConstants.PF_CONFIGURATOR_ADDER_ID + "Previous", dsData.Adder))
        parameters.add(createParameter(lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID, dsData.AdderUOM, uomsPerMaterial))
        parameters.add(createParameter(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID, dsData.RecalculationDate, dateOptions))
        parameters.add(createParameterWithMap(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID, dsData.RecalculationPeriod, dropdownOptions["RecalculationPeriod"] as Map))

        parameters.add(createHiddenParameter(lineItemConstants.INCO_TERM_ID, dsData.IncoTerm))
        parameters.add(createHiddenParameter(lineItemConstants.FREIGHT_TERM_ID, freightTerm))

        def filteredApproversMap = approversMap?.get(dsData.Division ?: "")?.get(dsData.SalesOrg ?: "")

        def conditionalParams = [
                Product           : productMasterData?.get(line?.Material),
                Plant             : plantOptions,
                PricingAndSalesUOM: uomsPerMaterial,
                FreightUOM        : uoms,
                ContractData      : dsData,
                ApproversMap      : filteredApproversMap,
        ]

        parameters.add([
                "name"    : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME,
                "label"   : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME,
                "type"    : InputType.CONFIGURATOR,
                "url"     : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_URL,
                "readOnly": false,
                "value"   : params + conditionalParams,
        ])

        // Scales Configurator
        def scalesParams = [
                UOM             : line.PriceType == "2" ? uomsPerMaterial : null,
                MOQUOM          : line.PriceType == "2" ? dsData.MOQUOM : null,
                PriceUOM        : line.PriceType == "2" ? dsData.PricingUOM : null,
                PriceType       : line.PriceType,
                NumberOfDecimals: dsData.NumberofDecimals?.toString()?.isNumber() ? dsData.NumberofDecimals?.toString().toInteger() : 2
        ]

        def pricingMap = out.InitZBPLMerged

        def defaultScalesData = defaultScalesData(line?.Material, line.PriceType, dsData.PriceListPLT, dsData.QuoteID, line?.LineID,
                dsData.SalesOrg ?: "", dsData.MOQ, dsData.MOQUOM, pricingMap, out.FindInitZBPLScales,
                out.FindInitQuotes, out.FindInitQuoteScales, uomConversionTable, globalUOMConversionTable)

        parameters.add([
                "name"    : lineItemConstants.SCALES_CONFIGURATOR_NAME,
                "label"   : lineItemConstants.SCALES_CONFIGURATOR_NAME,
                "type"    : InputType.CONFIGURATOR,
                "url"     : lineItemConstants.SCALES_CONFIGURATOR_URL,
                "readOnly": line.PriceType != "2",
                "value"   : defaultScalesData + scalesParams,
        ])

        quoteProcessor.addLineItem("ROOT", line.Material as String, parameters)
    }
}

return null

def updateOptionInput(String lineId, name, options) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "valueOptions": options,
    ])
}

def updateMapOptionInput(String lineId, name, Map options) {
    String[] x = options.keySet().collect { it.toString() }.toArray(new String[0])
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "valueOptions": x,
            "labels"      : options
    ])
}

def updateValue(String lineId, name, defaultValue, previousValue) {
    if (previousValue) return
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : defaultValue,
    ])
}

def createParameter(name, value) {
    return [name:name, value:value]
}

def createParameter(name, value, valueOptions) {
    return [name:name, value:value, valueOptions:valueOptions]
}

def createParameterWithMap(name, value, Map options) {
    String[] x = options.keySet().collect { it.toString() }.toArray(new String[0])
    return [name:name, value:value, valueOptions:x, "labels": options]
}

def createHiddenParameter(name, value) {
    return [name:name + "Hidden", type:InputType.HIDDEN, value:value]
}

def findShippingPoint(shippingPointNames, plant) {
    final tablesConstants = libs.QuoteConstantsLibrary.Tables
    final calculations = libs.QuoteLibrary.Calculations

    def filter = Filter.equal("key1", calculations.removePlantDescription([plant])?.find())

    def fields = ["key1", "key2"]
    def shippingPoints = api.findLookupTableValues(tablesConstants.PLANT_SHIPPING_POINT, fields, null, filter)?.key2

    return shippingPoints?.collect { shippingPointNames?.get(it) }?.findAll()?.sort()
}

def defaultScalesData(sku, priceType, pricelist, quoteID, lineID, salesOrg, moq, moqUOM, basePricing, zbplScales, quotes, quoteScales, uomConversionMap, globalUOMConversionMap) {
    final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
    final calculations = libs.QuoteLibrary.Calculations

    def previousValues = [:]
    previousValues.put(lineItemConstants.SCALES_ID, [])

    if (pricelist || priceType == "2") {
        def key, pricingData, scalesData
        def scaleUOM = null
        if (priceType == "2") {
            key = quoteID + "|" + lineID
            pricingData = quotes?.get(key)
            scalesData = calculations.getScalesData(pricingData, quoteScales)
            scaleUOM = scalesData?.find()?.ScaleUOM
        } else if (priceType == "3") {
            key = salesOrg + "|" + pricelist + "|" + sku
            pricingData = basePricing?.get(key)?.max { it.ValidFrom }
            scalesData = calculations.getScalesData(pricingData, zbplScales)
            scaleUOM = pricingData?.ScaleUOM
        }

        if (scalesData) {
            def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(sku, scaleUOM, moqUOM, uomConversionMap, globalUOMConversionMap) ?: 1
            def pricingScales = []
            scalesData?.each { data ->
                if ((data.ScaleQuantity * conversionFactor) >= moq) {
                    def scale = [
                            ScaleQty: data.ScaleQuantity,
                            ScaleUOM: scaleUOM,
                            Price   : data.ConditionRate,
                            PriceUOM: pricingData?.UOM,
                    ]
                    pricingScales.add(scale)
                }
            }

            previousValues.put(lineItemConstants.SCALES_ID, pricingScales)
        }
    }

    return previousValues
}
