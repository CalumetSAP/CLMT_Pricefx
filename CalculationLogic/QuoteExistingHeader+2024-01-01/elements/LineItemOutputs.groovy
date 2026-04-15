if (!quoteProcessor.isPrePhase()) return

final createOutput = libs.BdpLib.QuoteOutput
final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final query = libs.QuoteLibrary.Query

def productMasterData = out.FindProductMasterData ?: [:]
def customerMasterData = api.local.shipToOutputData ?: [:]
def salesPersonOptions = api.local.salesPersonTable && !api.isInputGenerationExecution() ? api.local.salesPersonTable as List : []
def shippingPointNames = out.FindShippingPoint && !api.isInputGenerationExecution() ? out.FindShippingPoint as Map : [:]
def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]
def meansOfTransportationOptions = out.FindMeansOfTransportation && !api.isInputGenerationExecution() ? out.FindMeansOfTransportation as Map : [:]
def modeOfTransportationOptions = out.FindModeOfTransportation && !api.isInputGenerationExecution() ? out.FindModeOfTransportation as Map : [:]

def dsData, configurator, productMasterItem, customerMasterItem, shipTo, key, useConfiguratorValue
def outputsMap = [:]

def priceType, meansOfTransportation, modeOfTransportation, rejectionReason, referencePeriod, freightTerm, adder
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    useConfiguratorValue = api.local.lineItemChanged == lnProduct.lineId

    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)

    key = dsData?.get("SAPContractNumber") + "|" + dsData?.get("SAPLineId")

    shipTo = dsData?.get("ShipTo")
    productMasterItem = productMasterData?.get(lnProduct.sku)
    customerMasterItem = customerMasterData?.get(shipTo)

    priceType = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
    meansOfTransportation = calculations.getInputValue(lnProduct, lineItemConstants.MEANS_OF_TRANSPORTATION_ID)
    modeOfTransportation = calculations.getInputValue(lnProduct, lineItemConstants.MODE_OF_TRANSPORTATION_ID)
    rejectionReason = calculations.getInputValue(lnProduct, lineItemConstants.REJECTION_REASON_ID)
    referencePeriod = calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)
    freightTerm = calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_TERM_ID)

    priceType = priceType ? dropdownOptions["PriceType"]?.find { k, v -> v.toString().startsWith(priceType as String) }?.key : priceType
    meansOfTransportation = meansOfTransportation ? meansOfTransportationOptions?.find { k, v -> v.toString().startsWith(meansOfTransportation as String) }?.key : meansOfTransportation
    modeOfTransportation = modeOfTransportation ? modeOfTransportationOptions?.find { k, v -> v.toString().startsWith(modeOfTransportation as String) }?.key : modeOfTransportation
    rejectionReason = rejectionReason ? dropdownOptions["RejectionReason"]?.find { k, v -> v.toString().startsWith(rejectionReason as String) }?.key : rejectionReason
    referencePeriod = referencePeriod ? dropdownOptions["ReferencePeriod"]?.find { k, v -> v.toString().startsWith(referencePeriod as String) }?.key : referencePeriod
    freightTerm = freightTerm ? dropdownOptions["FreightTerm"]?.find { k, v -> v.toString().startsWith(freightTerm as String) }?.key : freightTerm

    adder = useConfiguratorValue
            ? configurator?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_ID)
            : calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_ADDER_ID)

    outputsMap?.put(key, [
            (lineItemConstants.SAP_CONTRACT_ID)                      : dsData?.get("SAPContractNumber"),
            (lineItemConstants.LINE_NUMBER_ID)                       : dsData?.get("SAPLineId"),
            (lineItemConstants.PLANT_ID)                             : dsData?.get("Plant"),
            (lineItemConstants.SHIP_TO_ID)                           : shipTo,
            (lineItemConstants.PH1_ID)                               : productMasterItem?.get("PH1Code") + " - " + productMasterItem?.get("PH1Description"),
            (lineItemConstants.PH2_ID)                               : productMasterItem?.get("PH2Code") + " - " + productMasterItem?.get("PH2Description"),
            (lineItemConstants.PH3_ID)                               : productMasterItem?.get("PH3Code") + " - " + productMasterItem?.get("PH3Description"),
            (lineItemConstants.PH4_ID)                               : productMasterItem?.get("PH4Code") + " - " + productMasterItem?.get("PH4Description"),
            (lineItemConstants.MATERIAL_PACKAGE_STYLE_ID)            : productMasterItem?.get("MaterialPackageStyle"),
            (lineItemOutputsConstants.CONTAINER_DESCRIPTION_ID)      : productMasterItem?.get("ContainerDescription"),
            (lineItemConstants.LEGACY_MATERIAL_NUMBER_ID)            : productMasterItem?.get("LegacyMaterialNumber"),
            (lineItemOutputsConstants.WEIGHT_PER_GALLON_ID)          : productMasterItem?.get("NetWeight"),
            (lineItemConstants.COST_ID)                              : api.local.costMap?.get(lnProduct.lineId),
            (lineItemConstants.MATERIAL_MARGIN_ID)                   : api.local.materialMarginMap?.get(lnProduct.lineId),

            (lineItemConstants.THIRD_PARTY_CUSTOMER_ID)              : calculations.getInputValue(lnProduct, lineItemConstants.THIRD_PARTY_CUSTOMER_ID),
            (lineItemConstants.MOQ_ID)                               : calculations.getInputValue(lnProduct, lineItemConstants.MOQ_ID),
            (lineItemConstants.MOQ_UOM_ID)                           : calculations.getInputValue(lnProduct, lineItemConstants.MOQ_UOM_ID),
            (lineItemConstants.MEANS_OF_TRANSPORTATION_ID)           : meansOfTransportation,
            (lineItemConstants.MODE_OF_TRANSPORTATION_ID)            : modeOfTransportation,
            (lineItemConstants.PRICE_TYPE_ID)                        : priceType,
            (lineItemConstants.PRICE_LIST_ID)                        : calculations.getInputValue(lnProduct, lineItemConstants.PRICE_LIST_ID),
            (lineItemConstants.PRICE_ID)                             : calculations.getInputValue(lnProduct, lineItemConstants.PRICE_ID),
            (lineItemConstants.PRICING_UOM_ID)                       : calculations.getInputValue(lnProduct, lineItemConstants.PRICING_UOM_ID),
            (lineItemConstants.DELIVERED_PRICE_ID)                   : calculations.getInputValue(lnProduct, lineItemConstants.DELIVERED_PRICE_ID),
            (lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)      : calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID),
            (lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)  : referencePeriod,
            (lineItemConstants.PF_CONFIGURATOR_ADDER_ID)             : adder,
            (lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID)         : calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID),
            (lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID): calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID),
            (lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID)          : calculations.getInputValue(lnProduct, lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID),
            (lineItemConstants.NUMBER_OF_DECIMALS_ID)                : calculations.getInputValue(lnProduct, lineItemConstants.NUMBER_OF_DECIMALS_ID),
            (lineItemConstants.SHIPPING_POINT_ID)                    : calculations.getInputValue(lnProduct, lineItemConstants.SHIPPING_POINT_ID),
            (lineItemConstants.PER_ID)                               : calculations.getInputValue(lnProduct, lineItemConstants.PER_ID),
            (lineItemConstants.CURRENCY_ID)                          : calculations.getInputValue(lnProduct, lineItemConstants.CURRENCY_ID),
            (lineItemConstants.PRICE_VALID_FROM_ID)                  : calculations.getInputValue(lnProduct, lineItemConstants.PRICE_VALID_FROM_ID),
            (lineItemConstants.PRICE_VALID_TO_ID)                    : calculations.getInputValue(lnProduct, lineItemConstants.PRICE_VALID_TO_ID),
            (lineItemConstants.COMPETITOR_PRICE_ID)                  : calculations.getInputValue(lnProduct, lineItemConstants.COMPETITOR_PRICE_ID),
            (lineItemConstants.NAMED_PLACE_ID)                       : calculations.getInputValue(lnProduct, lineItemConstants.NAMED_PLACE_ID),
            (lineItemConstants.FREIGHT_ESTIMATE_ID)                  : calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_ESTIMATE_ID),
            (lineItemConstants.SALES_PERSON_ID)                      : calculations.getInputValue(lnProduct, lineItemConstants.SALES_PERSON_ID),

            (lineItemConstants.SHIP_TO_ADDRESS_ID)                   : customerMasterItem?.get("Address"),
            (lineItemConstants.SHIP_TO_CITY_ID)                      : customerMasterItem?.get("City"),
            (lineItemConstants.SHIP_TO_STATE_ID)                     : customerMasterItem?.get("State"),
            (lineItemConstants.SHIP_TO_ZIP_ID)                       : customerMasterItem?.get("Zip"),
            (lineItemConstants.SHIP_TO_COUNTRY_ID)                   : customerMasterItem?.get("Country"),
    ])
}

// Import line items
def plant, shippingPointOptions, shippingPoint, salesPerson, numberOfDecimals, recommendedValues
if (api.local.addedContracts) {
    def recommendedValuesMap = out.CalculateInitRecommendedPrice ?: [:]
    def contracts = out.FindContractDSData
    contracts?.each { contractNumber, lines ->
        lines?.each { line ->
            key = line.SAPContract + "|" + line.LineNumber

            shipTo = line.ShipTo
            productMasterItem = productMasterData?.get(line.Material)
            customerMasterItem = customerMasterData?.get(shipTo)

            plant = api.local.plantNames?.getOrDefault(line.Plant, line.Plant)

            shippingPointOptions = findShippingPoint(shippingPointNames, plant)
            shippingPoint = line.ShippingPoint ? shippingPointOptions?.find { it?.toString()?.startsWith(line.ShippingPoint) } : line.ShippingPoint

            salesPerson = line?.SalesPerson ? (salesPersonOptions?.find { it.toString().startsWith(line?.SalesPerson) } ?: line?.SalesPerson) : line?.SalesPerson

            numberOfDecimals = line?.NumberofDecimals ?: 2
            recommendedValues = recommendedValuesMap?.get(contractNumber + "|" + line?.LineNumber)

            outputsMap?.put(key, [
                    (lineItemConstants.SAP_CONTRACT_ID)                      : line.SAPContract,
                    (lineItemConstants.LINE_NUMBER_ID)                       : line.LineNumber,
                    (lineItemConstants.SHIP_TO_ID)                           : shipTo,
                    (lineItemConstants.PH1_ID)                               : productMasterItem?.get("PH1Code") + " - " + productMasterItem?.get("PH1Description"),
                    (lineItemConstants.PH2_ID)                               : productMasterItem?.get("PH2Code") + " - " + productMasterItem?.get("PH2Description"),
                    (lineItemConstants.PH3_ID)                               : productMasterItem?.get("PH3Code") + " - " + productMasterItem?.get("PH3Description"),
                    (lineItemConstants.PH4_ID)                               : productMasterItem?.get("PH4Code") + " - " + productMasterItem?.get("PH4Description"),
                    (lineItemConstants.MATERIAL_PACKAGE_STYLE_ID)            : productMasterItem?.get("MaterialPackageStyle"),
                    (lineItemOutputsConstants.CONTAINER_DESCRIPTION_ID)      : productMasterItem?.get("ContainerDescription"),
                    (lineItemConstants.LEGACY_MATERIAL_NUMBER_ID)            : productMasterItem?.get("LegacyMaterialNumber"),
                    (lineItemOutputsConstants.WEIGHT_PER_GALLON_ID)          : productMasterItem?.get("NetWeight"),

                    (lineItemConstants.THIRD_PARTY_CUSTOMER_ID)              : line.ThirdPartyCustomer,
                    (lineItemConstants.RECOMMENDED_PRICE_ID)                 : recommendedValues?.RecommendedPrice ? libs.QuoteLibrary.RoundingUtils.round(recommendedValues?.RecommendedPrice?.toBigDecimal(), numberOfDecimals?.toInteger())?.toString() : null,
                    (lineItemConstants.MOQ_ID)                               : line.MOQ,
                    (lineItemConstants.MOQ_UOM_ID)                           : line.MOQUOM,
                    (lineItemConstants.MEANS_OF_TRANSPORTATION_ID)           : line.MeansOfTransportation,
                    (lineItemConstants.MODE_OF_TRANSPORTATION_ID)            : line.ModeOfTransportation,
                    (lineItemConstants.PRICE_TYPE_ID)                        : line.PriceType,
                    (lineItemConstants.PRICE_LIST_ID)                        : line.PriceListPLT,
                    (lineItemConstants.PRICE_ID)                             : line.Price,
                    (lineItemConstants.PRICING_UOM_ID)                       : line.PricingUOM,
                    (lineItemConstants.DELIVERED_PRICE_ID)                   : line.DeliveredPrice,
                    (lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)      : [line?.IndexNumberOne, line?.IndexNumberTwo, line?.IndexNumberThree],
                    (lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)  : line.ReferencePeriod,
                    (lineItemConstants.PF_CONFIGURATOR_ADDER_ID)             : line.Adder,
                    (lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID)         : line.AdderUOM,
                    (lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID): line.RecalculationDate,
                    "ApprovalSequence"                                       : line.ApprovalSequence,
                    (lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID)          : line.CustomerMaterial,
                    (lineItemConstants.NUMBER_OF_DECIMALS_ID)                : line.NumberofDecimals,
                    (lineItemConstants.SHIPPING_POINT_ID)                    : shippingPoint,
                    (lineItemConstants.PER_ID)                               : line.Per,
                    (lineItemConstants.CURRENCY_ID)                          : line.Currency,
                    (lineItemConstants.PRICE_VALID_FROM_ID)                  : line.PriceValidFrom,
                    (lineItemConstants.PRICE_VALID_TO_ID)                    : line.PriceValidTo,
                    (lineItemConstants.COMPETITOR_PRICE_ID)                  : line.CompetitorPrice,
                    (lineItemConstants.NAMED_PLACE_ID)                       : line.NamedPlace,
                    (lineItemConstants.FREIGHT_ESTIMATE_ID)                  : line.FreightEstimate,
                    (lineItemConstants.SALES_PERSON_ID)                      : salesPerson,

                    (lineItemConstants.SHIP_TO_ADDRESS_ID)                   : customerMasterItem?.get("Address"),
                    (lineItemConstants.SHIP_TO_CITY_ID)                      : customerMasterItem?.get("City"),
                    (lineItemConstants.SHIP_TO_STATE_ID)                     : customerMasterItem?.get("State"),
                    (lineItemConstants.SHIP_TO_ZIP_ID)                       : customerMasterItem?.get("Zip"),
                    (lineItemConstants.SHIP_TO_COUNTRY_ID)                   : customerMasterItem?.get("Country"),
            ])

        }
    }
}

api.global.outputsMap = outputsMap

return null

def updateInputValue(String lineId, name, defaultValue, previousValue) {
    if (previousValue) return
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : defaultValue,
    ])
}

def findShippingPoint(shippingPointNames, plant) {
    final tablesConstants = libs.QuoteConstantsLibrary.Tables
    final calculations = libs.QuoteLibrary.Calculations

    def filter = Filter.equal("key1", calculations.removePlantDescription([plant])?.find())

    def fields = ["key1", "key2"]
    def shippingPoints = api.findLookupTableValues(tablesConstants.PLANT_SHIPPING_POINT, fields, null, filter)?.key2

    return shippingPoints?.collect { shippingPointNames?.get(it) }?.findAll()?.sort()
}
