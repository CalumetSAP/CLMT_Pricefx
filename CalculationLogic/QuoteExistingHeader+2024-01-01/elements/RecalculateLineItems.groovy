if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]

def readOnlyPricelist, readOnlyIndex, priceType, priceTypeAux, configurator, freightTerm, incoterm
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId

    def useConfiguratorValue = api.local.lineItemChanged == lineId

    configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)

    priceTypeAux = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
    priceType = useConfiguratorValue
            ? configurator?.get(lineItemConstants.PRICE_TYPE_ID)
            : priceTypeAux ? dropdownOptions["PriceType"]?.find { k, v -> v.toString().startsWith(priceTypeAux as String) }?.key : priceTypeAux
    readOnlyPricelist = priceType == "3"

    updateReadOnlyInput(lineId, lineItemConstants.PRICE_ID, readOnlyPricelist)
    updateReadOnlyInput(lineId, lineItemConstants.DELIVERED_PRICE_ID, readOnlyPricelist)
    updateReadOnlyInput(lineId, lineItemConstants.PRICING_UOM_ID, readOnlyPricelist)
    updateReadOnlyInput(lineId, lineItemConstants.NUMBER_OF_DECIMALS_ID, readOnlyPricelist)

    if (readOnlyPricelist) updateValue(lineId, lineItemConstants.NUMBER_OF_DECIMALS_ID, "2")

    readOnlyIndex = !(priceType == "1")

    updateReadOnlyInput(lineId, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, readOnlyIndex, true)
    updateReadOnlyInput(lineId, lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID, readOnlyIndex, true)
    updateReadOnlyInput(lineId, lineItemConstants.PF_CONFIGURATOR_ADDER_ID, readOnlyIndex, true)
    updateReadOnlyInput(lineId, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID, readOnlyIndex, true)
    updateReadOnlyInput(lineId, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID, readOnlyIndex, true)
    updateReadOnlyInput(lineId, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID, readOnlyIndex, true)
    if (priceType == "1") {
        updateReadOnlyInput(lineId, lineItemConstants.PRICE_LIST_ID, true)
        updateValue(lineId, lineItemConstants.PRICE_LIST_ID, null)
    }


    if (priceType == "4") {
        updateReadOnlyInput(lineId, lineItemConstants.PRICE_ID, true)
        updateReadOnlyInput(lineId, lineItemConstants.COMPETITOR_PRICE_ID, true)
        updateReadOnlyInput(lineId, lineItemConstants.DELIVERED_PRICE_ID, true)
        updateReadOnlyInput(lineId, lineItemConstants.PRICING_UOM_ID, true)
        updateReadOnlyInput(lineId, lineItemConstants.PRICE_VALID_FROM_ID, true)
        updateReadOnlyInput(lineId, lineItemConstants.PRICE_VALID_TO_ID, true)
        updateReadOnlyInput(lineId, lineItemConstants.PER_ID, true)
        updateReadOnlyInput(lineId, lineItemConstants.CURRENCY_ID, true)
        updateReadOnlyInput(lineId, lineItemConstants.NUMBER_OF_DECIMALS_ID, true)
    }

    freightTerm = calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_TERM_ID)
    incoterm = calculations.getInputValue(lnProduct, lineItemConstants.INCO_TERM_ID)
    if (freightTerm && !incoterm) updateIncoterm(lineId, freightTerm, dropdownOptions)

    def priceTypeHasChanged = priceTypeHasChanged(lnProduct, dropdownOptions["PriceType"], useConfiguratorValue)
    if (priceTypeHasChanged && (priceType != "3" && priceType != "2")) {
        updateValue(lineId, lineItemConstants.PRICE_LIST_ID, null)
    }

    // Is rejected line
    def rejectionReason = findValue(useConfiguratorValue, lineItemConstants.REJECTION_REASON_ID, lnProduct, configurator)
    def isRejectedLine = rejectionReason != null
    updateValue(lineId, lineItemConstants.LINE_IS_REJECTED_ID, isRejectedLine)
}

return null

def updateReadOnlyInput(String lineId, name, readOnly, resetValue = false) {
    if (readOnly && resetValue) {
        quoteProcessor.addOrUpdateInput(
                lineId, [
                "name"    : name,
                "readOnly": readOnly,
                "value"   : null
        ])
    } else {
        quoteProcessor.addOrUpdateInput(
                lineId, [
                "name"    : name,
                "readOnly": readOnly,
        ])
    }
}

def updateValue(String lineId, name, value) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name" : name,
            "value": value,
    ])
}

def updateIncoterm(String lineId, freightTerm, dropdownOptions) {
    final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

    def freightTermAux = dropdownOptions["FreightTerm"]?.find { k, v -> v.toString().startsWith(freightTerm) }?.key
    def updatedValue = null
    switch (freightTermAux) {
        case "1":
            updatedValue = "FCA"
            break
        case "2":
            updatedValue = "CPT"
            break
        case "3":
            updatedValue = "DAP"
            break
        case "4":
            updatedValue = "DAP"
            break
    }
    updateValue(lineId, lineItemConstants.INCO_TERM_ID, updatedValue)
}

def priceTypeHasChanged(lnProduct, priceTypeMap, useConfiguratorValue) {
    final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
    final calculations = libs.QuoteLibrary.Calculations

    def configuratorValues = lnProduct.inputs.find {
        lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    def priceTypeAux = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID) as String
    def comparePriceType = useConfiguratorValue
            ? configuratorValues?.get(lineItemConstants.PRICE_TYPE_ID)
            : priceTypeAux ? priceTypeMap?.find { k, v -> v.toString().startsWith(priceTypeAux) }?.key : priceTypeAux
    def previousPriceType = calculations.getInputValue(lnProduct, lineItemConstants.PREVIOUS_PRICE_TYPE_ID)
    if (comparePriceType != previousPriceType) return true

    return false
}

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}