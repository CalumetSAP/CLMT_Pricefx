if (!quoteProcessor.isPrePhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]

def approvalRequiredMap = [:]

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def headerHasChanged = headerConfigurator?.get(headerConstants.HEADER_HAS_CHANGED_ID)?.values()?.any { it }

def lineItemChanged = false
def dsData, useConfiguratorValue, configuratorValues
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String
    useConfiguratorValue = api.local.lineItemChanged == lineId
    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    configuratorValues = lnProduct.inputs.find {
        lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    def priceTypeAux = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
    def referencePeriodAux = calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)
    def freightTermAux = calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_TERM_ID)
    def rejectionReasonAux = calculations.getInputValue(lnProduct, lineItemConstants.REJECTION_REASON_ID)
    def comparePriceType = useConfiguratorValue
            ? configuratorValues?.get(lineItemConstants.PRICE_TYPE_ID)
            : priceTypeAux ? dropdownOptions["PriceType"]?.find { k, v -> v.toString().startsWith(priceTypeAux) }?.key : priceTypeAux

    def compareFreightTerm = useConfiguratorValue
            ? configuratorValues?.get(lineItemConstants.FREIGHT_TERM_ID)
            : freightTermAux ? dropdownOptions["FreightTerm"]?.find { k, v -> v.toString().startsWith(freightTermAux) }?.key : freightTermAux

    def compareRejectionReason = useConfiguratorValue
            ? configuratorValues?.get(lineItemConstants.REJECTION_REASON_ID)
            : rejectionReasonAux ? dropdownOptions["RejectionReason"]?.find { k, v -> v.toString().startsWith(rejectionReasonAux) }?.key : rejectionReasonAux

    def indexNumberOne = dsData.IndexNumberOne
    def indexNumberTwo = dsData.IndexNumberTwo
    def indexNumberThree = dsData.IndexNumberThree
    def indexList = []
    if (indexNumberOne) indexList.add(indexNumberOne)
    if (indexNumberTwo) indexList.add(indexNumberTwo)
    if (indexNumberThree) indexList.add(indexNumberThree)

    def indexValue = findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, lnProduct, configuratorValues) ?: []

    def compareReferencePeriod = useConfiguratorValue
            ? configuratorValues?.get(lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)
            : referencePeriodAux ? dropdownOptions["ReferencePeriod"]?.find { k, v -> v.toString().startsWith(referencePeriodAux) }?.key : referencePeriodAux

    def priceChecks = [
//            dsData?.PriceType != comparePriceType,
            dsData?.Price != findValue(useConfiguratorValue, lineItemConstants.PRICE_ID, lnProduct, configuratorValues),
//            dsData?.DeliveredPrice != findValue(useConfiguratorValue, lineItemConstants.DELIVERED_PRICE_ID, lnProduct, configuratorValues),
            dsData?.PricingUOM != findValue(useConfiguratorValue, lineItemConstants.PRICING_UOM_ID, lnProduct, configuratorValues),
            dsData?.Per != findValue(useConfiguratorValue, lineItemConstants.PER_ID, lnProduct, configuratorValues),
            dsData?.Currency != findValue(useConfiguratorValue, lineItemConstants.CURRENCY_ID, lnProduct, configuratorValues),
            dsData?.PriceValidFrom != findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configuratorValues),
            dsData?.PriceValidTo != findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_TO_ID, lnProduct, configuratorValues)
    ]

    def otherChecks = [
            dsData?.PriceType != comparePriceType,
            !(indexValue?.size() == indexList.size() && indexList.containsAll(indexValue)),
            dsData?.NumberofDecimals != findValue(useConfiguratorValue, lineItemConstants.NUMBER_OF_DECIMALS_ID, lnProduct, configuratorValues),
            freightTermHasChanged(dsData?.FreightTerm, compareFreightTerm)
    ]

    def sapChecks = [
            dsData?.PriceType != comparePriceType,
            !(indexValue?.size() == indexList.size() && indexList.containsAll(indexValue)),
            dsData?.NumberofDecimals != findValue(useConfiguratorValue, lineItemConstants.NUMBER_OF_DECIMALS_ID, lnProduct, configuratorValues),
            freightTermHasChanged(dsData?.FreightTerm, compareFreightTerm),
            dsData?.CustomerMaterial != findValue(useConfiguratorValue, lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID, lnProduct, configuratorValues),
            dsData?.ThirdPartyCustomer != findValue(useConfiguratorValue, lineItemConstants.THIRD_PARTY_CUSTOMER_ID, lnProduct, configuratorValues),
            dsData?.ShippingPoint != findValue(useConfiguratorValue, lineItemConstants.SHIPPING_POINT_ID, lnProduct, configuratorValues),
            dsData?.MOQ != findValue(useConfiguratorValue, lineItemConstants.MOQ_ID, lnProduct, configuratorValues),
            dsData?.MOQUOM != findValue(useConfiguratorValue, lineItemConstants.MOQ_UOM_ID, lnProduct, configuratorValues),
            dsData?.MeansOfTransportation != findValue(useConfiguratorValue, lineItemConstants.MEANS_OF_TRANSPORTATION_ID, lnProduct, configuratorValues)?.toString()?.split(" - ")?.getAt(0),
            dsData?.ModeOfTransportation != findValue(useConfiguratorValue, lineItemConstants.MODE_OF_TRANSPORTATION_ID, lnProduct, configuratorValues)?.toString()?.split(" - ")?.getAt(0),
            dsData?.NamedPlace != findValue(useConfiguratorValue, lineItemConstants.NAMED_PLACE_ID, lnProduct, configuratorValues),
            dsData?.FreightTerm != compareFreightTerm,
            dsData?.IncoTerm != findValue(useConfiguratorValue, lineItemConstants.INCO_TERM_ID, lnProduct, configuratorValues),
            dsData?.SalesPerson != findValue(useConfiguratorValue, lineItemConstants.SALES_PERSON_ID, lnProduct, configuratorValues),
            dsData?.RejectionReason != findValue(useConfiguratorValue, lineItemConstants.REJECTION_REASON_ID, lnProduct, configuratorValues)?.toString()?.split(" - ")?.getAt(0),
            dsData?.PriceListPLT != findValue(useConfiguratorValue, lineItemConstants.PRICE_LIST_ID, lnProduct, configuratorValues),
    ]

    if (comparePriceType == "1") {
        otherChecks.add(dsData?.ReferencePeriod != compareReferencePeriod)
        otherChecks.add(dsData?.Adder != findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_ADDER_ID, lnProduct, configuratorValues))
        otherChecks.add(dsData?.AdderUOM != findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID, lnProduct, configuratorValues))
        otherChecks.add(dsData?.RecalculationDate?.toString() != findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID, lnProduct, configuratorValues)?.toString())
        otherChecks.add(dsData?.RecalculationPeriod != findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID, lnProduct, configuratorValues))

        sapChecks.add(dsData?.ReferencePeriod != compareReferencePeriod)
        sapChecks.add(dsData?.Adder != findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_ADDER_ID, lnProduct, configuratorValues))
        sapChecks.add(dsData?.AdderUOM != findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID, lnProduct, configuratorValues))
        sapChecks.add(dsData?.RecalculationDate?.toString() != findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID, lnProduct, configuratorValues)?.toString())
        sapChecks.add(dsData?.RecalculationPeriod != findValue(useConfiguratorValue, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID, lnProduct, configuratorValues))
    }

    def priceChangeFlag = priceChecks.any { it }
    def freightChangesFlag = configuratorValues?.get(lineItemConstants.FREIGHT_HAS_CHANGED_ID) as Boolean
    def sapChangesFlag = sapChecks.any { it }

    def customerToIndex = dsData?.PriceType == "2" && comparePriceType == "1"
    def indexToCustomer = dsData?.PriceType == "1" && comparePriceType == "2"

    def somethingHasChanged = priceChecks.any { it } || otherChecks.any { it }
    if (otherChecks.any { it }) lineItemChanged = true

    def freightTermChangeFlag = freightTermHasChanged(dsData?.FreightTerm, compareFreightTerm)

    updateInputValue(lineId, lineItemConstants.LINE_HAS_CHANGED_ID, somethingHasChanged)
    updateInputValue(lineId, lineItemConstants.PRICE_TYPE_HAS_CHANGED_ID, dsData?.PriceType != comparePriceType)
    updateInputValue(lineId, lineItemConstants.PRICE_CHANGE_FLAG_ID, priceChangeFlag)
    updateInputValue(lineId, lineItemConstants.PT_CHANGED_FROM_CUSTOMER_TO_INDEX_ID, customerToIndex)
    updateInputValue(lineId, lineItemConstants.PT_CHANGED_FROM_INDEX_TO_CUSTOMER_ID, indexToCustomer)
    updateInputValue(lineId, lineItemConstants.FREIGHT_TERM_CHANGE_FLAG_ID, freightTermChangeFlag)
    updateInputValue(lineId, lineItemConstants.REJECTION_REASON_HAS_CHANGED_ID, dsData?.RejectionReason != compareRejectionReason)
    api.logInfo("AGUS - dsData?.RejectionReason", dsData?.RejectionReason)
    api.logInfo("AGUS - compareRejectionReason", compareRejectionReason)
    updateInputValue(lineId, lineItemConstants.FREIGHT_HAS_CHANGED_ID, freightChangesFlag)
    updateInputValue(lineId, lineItemConstants.SAP_CHANGES_FLAG_ID, sapChangesFlag)

    def isLineRejected = compareRejectionReason != null
    def approvalRequired = somethingHasChanged && !isLineRejected
    approvalRequiredMap.put(lineId, approvalRequired)
}

def isHeaderOnly = headerHasChanged && !lineItemChanged
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String

    updateInputValue(lineId, lineItemConstants.HEADER_ONLY_FLAG_ID, isHeaderOnly)
}

api.local.approvalRequiredMap = approvalRequiredMap

return null

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}

def updateInputValue(String lineId, name, defaultValue) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : defaultValue,
    ])
}

def freightTermHasChanged(previousFreight, currentFreight) {
    return ((previousFreight == "1" || previousFreight == "2") && (currentFreight == "3" || currentFreight == "4")) ||
            ((previousFreight == "3" || previousFreight == "4") && (currentFreight == "1" || currentFreight == "2"))
}