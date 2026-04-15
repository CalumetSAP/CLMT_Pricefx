import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

final headerConfiguratorConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final headerOutputsConstants = libs.QuoteConstantsLibrary.HeaderOutputs
final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem
final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

def quoteId, quoteType, uuids
def isExistingQuote = false
if (api.isDebugMode()) {
    quoteId = "2576.Q"
    quoteType = "New Contract"
    uuids = ["102771-311779-US30-20": "1B1C6195-FD91-4A8F-ADCA-372737A64AF7",
             "102771-307591-US30-20": "3623C22D-D674-4EAE-B11C-DA4C84CE5D4C",
             "40022331": "51E51CA7-6EED-4069-8B66-1219A05E5B08"]
} else {
    def calcItem = dist?.calcItem
    if (["OK", "SYNTAX_ERROR"].contains(calcItem?.Status)) return
    quoteId = calcItem?.Key2
    quoteType = calcItem?.Value?.quoteType
    uuids = libs.QuoteLibrary.Calculations.getContractsUUIDs(quoteId)
}

def withHyphens = uuids.findAll { key, _ -> key.contains("-") }
def withoutHyphens = uuids.findAll { key, _ -> !key.contains("-") }

if (quoteType != "New Contract" && quoteType != "NewContract") return

loader = api.isDebugMode() ? [] : dist.dataLoader

def quote = api.getCalculableLineItemCollection(quoteId)

if (quote.uniqueName != quote.rootUniqueName) {
    def supersededQuotes = getSupersededQuotes(quoteId, quote.rootUniqueName)
    def supersededRow
    def existingRows = getExistingRows(supersededQuotes)

    supersededQuotes?.each {supersededQuote ->
        def supersededQuoteItems = supersededQuote?.lineItems

        supersededQuoteItems?.each {supersededQuoteItem ->
            supersededRow = existingRows?.get(supersededQuote?.uniqueName + "|" + supersededQuoteItem?.lineId)
            if (!supersededRow) return
            supersededRow["SupersededInvalidatedby"] = supersededQuote?.supersededBy

            api.isDebugMode() ? loader.add(supersededRow) : loader.addRow(supersededRow)
        }
    }
}

api.trace("quote", quote)

def quoteItems = quote?.lineItems
def quoteInputConfigurator = getInputByName(quote?.inputs, headerConfiguratorConstants.INPUTS_NAME)
def quoteCompetitiveConfigurator = getInputByName(quote?.inputs, headerConfiguratorConstants.COMPETITIVE_INFO_NAME)

def dropdownOptions = getDropdownOptionsValues()
def freightTermMap = dropdownOptions?.FreightTerm
def referencePeriodMap = dropdownOptions?.ReferencePeriod
def deliveredFreightTerms = libs.QuoteLibrary.Query.getAllDeliveredFreightTerms()

def contractPricingDateMap = [:]

quoteItems.each { obj ->
    def configurator = getInputByName(obj?.inputs, lineItemInputsConstants.NEW_QUOTE_CONFIGURATOR_NAME)
    def key = configurator?.get(lineItemInputsConstants.SHIP_TO_ID) ?: "No-ShipTo"
    if (contractPricingDateMap.containsKey(key)) {
        if (configurator?.get(lineItemInputsConstants.PRICE_VALID_FROM_ID) > contractPricingDateMap[key]) {
            contractPricingDateMap[key] = configurator?.get(lineItemInputsConstants.PRICE_VALID_FROM_ID)
        }
    } else {
        contractPricingDateMap[key] = configurator?.get(lineItemInputsConstants.PRICE_VALID_FROM_ID)
    }
}

def row, outputs, lnConfigurator, indexList, shipToData, freightTermId, referencePeriodId, contractEffectiveDate,
        division, salesOrg, freightAmount, deliveredPrice
def sapContractNumber, sapLine, shipTo, plant, soldTo
def priceProtection, noOfDays, movementTiming, movementStart, movementDay
quoteItems?.each { quoteItem ->
    lnConfigurator = getInputByName(quoteItem?.inputs, lineItemInputsConstants.NEW_QUOTE_CONFIGURATOR_NAME)
    outputs = quoteItem?.outputs

    indexList = lnConfigurator?.get(lineItemInputsConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)

    shipTo = lnConfigurator?.get(lineItemInputsConstants.SHIP_TO_ID)
    shipToData = getCodeAndDescription(shipTo as String, " - ")

    freightTermId = lnConfigurator?.get(lineItemInputsConstants.FREIGHT_TERM_ID)
    referencePeriodId = lnConfigurator?.get(lineItemInputsConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)

    sapContractNumber = getOutputByName(outputs, "SAPContractNumber")
    sapLine = getOutputByName(outputs, "SAPLineId")

    priceProtection = getOutputByName(outputs, lineItemOutputsConstants.PP_PRICE_PROTECTION_ID)
    noOfDays = getOutputByName(outputs, lineItemOutputsConstants.PP_NUMBER_OF_DAYS_ID)
    movementTiming = getOutputByName(outputs, lineItemOutputsConstants.PP_MOVEMENT_TIMING_ID)
    movementStart = getOutputByName(outputs, lineItemOutputsConstants.PP_MOVEMENT_START_ID)
    movementDay = getOutputByName(outputs, lineItemOutputsConstants.PP_MOVEMENT_DAY_ID)

    soldTo = quoteInputConfigurator?.get(headerConfiguratorConstants.SOLD_TO_ID)

    plant = getCode(lnConfigurator?.get(lineItemInputsConstants.PLANT_ID) as String, " - ")

    contractEffectiveDate = quoteInputConfigurator?.get(headerConfiguratorConstants.CONTRACT_EFFECTIVE_DATE_ID)

    division = quoteInputConfigurator?.get(headerConfiguratorConstants.DIVISION_ID)
    salesOrg = quoteInputConfigurator?.get(headerConfiguratorConstants.SALES_ORG_ID)
    freightAmount = lnConfigurator?.get(lineItemInputsConstants.FREIGHT_AMOUNT_ID)

    deliveredPrice = deliveredFreightTerms?.contains(freightTermId)
            ? lnConfigurator?.get(lineItemInputsConstants.DELIVERED_PRICE_ID)
            : lnConfigurator?.get(lineItemInputsConstants.PRICE_ID)

    def key = """${soldTo}-${shipToData.Code}-${salesOrg}-${division}"""
    api.trace("key?.toString()", null, key?.toString())
    def uuid = withoutHyphens?.get(sapContractNumber)?:withHyphens?.get(key?.toString())?:withoutHyphens?.get(libs.QuoteLibrary.Calculations.EMPTY)?.toString()

    api.trace("uuid", null, uuid)

    row = [
            "QuoteID"                                      : quote?.uniqueName,
            "UpdatedbyID"                                  : quote?.uniqueName,
            "UpdatedbyProcess"                             : "Quote",
            "QuoteIDwoRevision"                            : quote?.rootUniqueName,
            "QuoteType"                                    : quote?.quoteType,
            "LineID"                                       : quoteItem?.lineId,
            "QuoteCreationDate"                            : quote?.createDate,
            "ContractPricingDate"                          : contractPricingDateMap?.get(shipTo ?: "No-ShipTo"),
            "Createdby"                                    : quote?.createdByName,
            "QuoteLastUpdate"                              : quote?.lastUpdateDate,
            "LastUpdatebyName"                             : quote?.lastUpdateByName,
            "QuoteLabel"                                   : quote?.label,
            "SupersededInvalidatedby"                      : quote?.supersededBy,
            "EffectiveDate"                                : quote?.targetDate,
            "ExpiryDate"                                   : quote?.expiryDate,
            "ContractEffectiveDate"                        : contractEffectiveDate,
            "ContractExpiryDate"                           : sdf.parse("9999-12-31"),
            "SoldTo"                                       : soldTo,
            "ShipTo"                                       : shipToData.Code,
            "Division"                                     : division,
            "SalesOrg"                                     : salesOrg,
            "ExternalNotes"                                : quoteInputConfigurator?.get(headerConfiguratorConstants.EXTERNAL_NOTES_ID),
            "PrimaryCompetitor"                            : quoteCompetitiveConfigurator?.get(headerConfiguratorConstants.PRIMARY_COMPETITOR_ID),
            "CompetitiveSituation"                         : quoteCompetitiveConfigurator?.get(headerConfiguratorConstants.COMPETITIVE_SITUATION_ID),
            "ThirdPartyCustomer"                           : lnConfigurator?.get(lineItemInputsConstants.THIRD_PARTY_CUSTOMER_ID),
            "ApprovalSequence"                             : getOutputByName(outputs, lineItemOutputsConstants.APPROVAL_SEQUENCE_ID),
            "DiscountApprover"                             : getOutputByName(outputs, lineItemOutputsConstants.APPROVER_ID),
            "CompetitorPrice"                              : lnConfigurator?.get(lineItemInputsConstants.COMPETITOR_PRICE_ID),
            "Cost"                                         : getOutputByName(outputs, lineItemOutputsConstants.COST_ID),
            "Currency"                                     : lnConfigurator?.get(lineItemInputsConstants.CURRENCY_ID),
            "CustomerMaterial"                             : lnConfigurator?.get(lineItemInputsConstants.CUSTOMER_MATERIAL_NUMBER_ID),
            "FreightAmount"                                : freightAmount,
            "FreightEstimate"                              : lnConfigurator?.get(lineItemInputsConstants.FREIGHT_ESTIMATE_ID),
            "FreightTerm"                                  : freightTermId,
            "FreightTermValue"                             : freightTermMap?.get(freightTermId),
            "FreightUOM"                                   : freightAmount ? lnConfigurator?.get(lineItemInputsConstants.FREIGHT_UOM_ID) : null,
            "FreightValidto"                               : freightAmount ? lnConfigurator?.get(lineItemInputsConstants.FREIGHT_VALID_TO_ID) : null,
            "FreightValidFrom"                             : freightAmount ? lnConfigurator?.get(lineItemInputsConstants.FREIGHT_VALID_FROM_ID) : null,
            "GuardrailPrice"                               : getOutputByName(outputs, lineItemOutputsConstants.RECOMMENDED_PRICE_ID),
            "Incoterm"                                     : lnConfigurator?.get(lineItemInputsConstants.INCO_TERM_ID),
            "MaterialPackageStyle"                         : getOutputByName(outputs, lineItemOutputsConstants.MATERIAL_PACKAGE_STYLE_ID),
            "MeansOfTransportation"                        : lnConfigurator?.get(lineItemInputsConstants.MEANS_OF_TRANSPORTATION_ID),
            "ModeOfTransportation"                         : lnConfigurator?.get(lineItemInputsConstants.MODE_OF_TRANSPORTATION_ID),
            "MOQ"                                          : lnConfigurator?.get(lineItemInputsConstants.MOQ_ID),
            "MOQUOM"                                       : lnConfigurator?.get(lineItemInputsConstants.MOQ_UOM_ID),
            "NamedPlace"                                   : lnConfigurator?.get(lineItemInputsConstants.NAMED_PLACE_ID),
            "NumberofDecimals"                             : lnConfigurator?.get(lineItemInputsConstants.NUMBER_OF_DECIMALS_ID),
            "Plant"                                        : plant,
            "Price"                                        : lnConfigurator?.get(lineItemInputsConstants.PRICE_ID),
            "DeliveredPrice"                               : deliveredPrice,
            "PriceListPLT"                                 : lnConfigurator?.get(lineItemInputsConstants.PRICE_LIST_ID),
            "PriceValidFrom"                               : lnConfigurator?.get(lineItemInputsConstants.PRICE_VALID_FROM_ID),
            "PriceValidTo"                                 : lnConfigurator?.get(lineItemInputsConstants.PRICE_VALID_TO_ID),
            "PricingUOM"                                   : lnConfigurator?.get(lineItemInputsConstants.PRICING_UOM_ID),
            "SalesPerson"                                  : getCode(lnConfigurator?.get(lineItemInputsConstants.SALES_PERSON_ID) as String, " - "),
            "SAPContractNumber"                            : sapContractNumber,
            "ShippingPoint"                                : getCode(lnConfigurator?.get(lineItemInputsConstants.SHIPPING_POINT_ID) as String, " - "),
            "Material"                                     : quoteItem?.sku,
            "SoldtoName"                                   : getSoldToName(soldTo),
            "ShiptoName"                                   : shipToData.Description,
            "MaterialDescription"                          : quoteItem?.label,
            "SAPLineID"                                    : sapLine,
            "Per"                                          : lnConfigurator?.get(lineItemInputsConstants.PER_ID),
            "PriceType"                                    : lnConfigurator?.get(lineItemInputsConstants.PRICE_TYPE_ID),
            "IndexNumberOne"                               : indexList?.size() > 0 ? indexList.get(0) : null,
            "IndexNumberTwo"                               : indexList?.size() > 1 ? indexList.get(1) : null,
            "IndexNumberThree"                             : indexList?.size() > 2 ? indexList.get(2) : null,
            "IndexNumberOnePercent"                        : indexList?.size() == 1 ? 1.toBigDecimal() : (indexList?.size() == 2 ? 0.5.toBigDecimal() : (indexList?.size() == 3 ? (1/3).toBigDecimal() : null)),
            "IndexNumberTwoPercent"                        : indexList?.size() == 1 ? 0.toBigDecimal() : (indexList?.size() == 2 ? 0.5.toBigDecimal() : (indexList?.size() == 3 ? (1/3).toBigDecimal() : null)),
            "IndexNumberThreePercent"                      : indexList?.size() == 1 ? 0.toBigDecimal() : (indexList?.size() == 2 ? 0.toBigDecimal() : (indexList?.size() == 3 ? (1/3).toBigDecimal() : null)),
            "ReferencePeriod"                              : referencePeriodId,
            "Adder"                                        : lnConfigurator?.get(lineItemInputsConstants.PF_CONFIGURATOR_ADDER_ID),
            "AdderUOM"                                     : lnConfigurator?.get(lineItemInputsConstants.PF_CONFIGURATOR_ADDER_UOM_ID),
            "AdderNumberofDecimals"                        : lnConfigurator?.get(lineItemInputsConstants.PF_CONFIGURATOR_ADDER_NUMBER_OF_DECIMAL_PLACES_ID),
            "RecalculationDate"                            : lnConfigurator?.get(lineItemInputsConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID),
            "RecalculationPeriod"                          : lnConfigurator?.get(lineItemInputsConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID),
            "ReferencePeriodValue"                         : referencePeriodMap?.get(referencePeriodId),
            "FormulaApprover"                              : getOutputByName(outputs, lineItemOutputsConstants.FORMULA_APPROVER_ID),
            "IndexIndicator"                               : lnConfigurator?.get(lineItemInputsConstants.INDEX_INDICATOR_ID),
            "uuid"                                         : uuid,
            "PriceProtection"                              : priceProtection,
            "NoOfDays"                                     : noOfDays,
            "MovementTiming"                               : movementTiming,
            "MovementStart"                                : movementStart,
            "MovementDay"                                  : movementDay,
    ]
    api.isDebugMode() ? loader.add(row) : loader.addRow(row)
}

api.trace("loader", loader)

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}

def getOutputByName(outputs, name) {
    return outputs?.find { it.resultName == name}?.result
}

def getCode (String value, String separator) {
    if (!value) {
        return null
    }
    return value?.split(separator)?.getAt(0)
}

def getCodeAndDescription(String value, String separator) {
    if (!value) {
        return [
                Code       : null,
                Description: null
        ]
    }
    def array = value?.split(separator)
    return [
            Code       : array.getAt(0),
            Description: array.size() > 1 ? array.getAt(1) : null
    ]
}

def getDropdownOptionsValues() {
    def tablesConstants = libs.QuoteConstantsLibrary.Tables

    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.in("key2", ["ReferencePeriod", "RejectionReason", "FreightTerm"]),
    ]
    def data = api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)

    data.inject([:]) { formatted, entry ->
        String key = entry["key2"]
        def value = [(entry["key3"]) : (entry["attribute1"])]
        formatted[key] = formatted.containsKey(key) ? formatted[key] + value : value
        formatted
    } ?: [:]
}

def getSupersededQuotes(typedId, rootUniqueName) {
    def filters = [
            Filter.or(
                    Filter.like("uniqueName", "%" + rootUniqueName + "-%"),
                    Filter.equal("uniqueName", rootUniqueName)
            ),
            Filter.notEqual("typedId", typedId)
    ]

    api.find("Q", 0, api.getMaxFindResultsLimit(), null, *filters, null)
}

def getExistingRows(supersededQuotes) {
    def tableConstants = libs.QuoteConstantsLibrary.Tables
    def ctx = api.getDatamartContext()
    def dataSource= ctx.getDataSource(tableConstants.DATA_SOURCE_QUOTES)
    def query = ctx.newQuery(dataSource, false)

    query.identity {
        selectAll(true)
        where(Filter.in("QuoteID", supersededQuotes?.uniqueName as List))
        where(Filter.in("LineID", supersededQuotes?.lineItems?.flatten().lineId as List))
    }

    def result = ctx.executeQuery(query).getData().toResultMatrix().getEntries()

    return result?.collectEntries{
        [(it.QuoteID + "|" + it.LineID): it]
    }
}

def getSoldToName(soldTo) {
    def customerFields = ["customerId", "name"]
    def customerFilter = Filter.equal("customerId", soldTo)

    def selectedCustomer = api.find("C", 0, 1, null, customerFields, customerFilter)?.find()

    return selectedCustomer?.name
}