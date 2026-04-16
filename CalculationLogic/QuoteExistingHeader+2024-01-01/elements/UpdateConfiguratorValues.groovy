import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || api.local.configuratorHasChanged) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]
def meansOfTransportationOptions = out.FindMeansOfTransportation && !api.isInputGenerationExecution() ? out.FindMeansOfTransportation as Map : [:]
def modeOfTransportationOptions = out.FindModeOfTransportation && !api.isInputGenerationExecution() ? out.FindModeOfTransportation as Map : [:]

def priceType, meansOfTransportation, modeOfTransportation, rejectionReason, referencePeriod, freightTerm
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def configuratorValues = lnProduct.inputs.find {
        lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

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

    configuratorValues.put(lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID, calculations.getInputValue(lnProduct, lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID))
    configuratorValues.put(lineItemConstants.THIRD_PARTY_CUSTOMER_ID, calculations.getInputValue(lnProduct, lineItemConstants.THIRD_PARTY_CUSTOMER_ID))
    configuratorValues.put(lineItemConstants.PLANT_ID, calculations.getInputValue(lnProduct, lineItemConstants.PLANT_ID))
    configuratorValues.put(lineItemConstants.SHIPPING_POINT_ID, calculations.getInputValue(lnProduct, lineItemConstants.SHIPPING_POINT_ID))
    configuratorValues.put(lineItemConstants.MOQ_ID, calculations.getInputValue(lnProduct, lineItemConstants.MOQ_ID))
    configuratorValues.put(lineItemConstants.MOQ_UOM_ID, calculations.getInputValue(lnProduct, lineItemConstants.MOQ_UOM_ID))
    configuratorValues.put(lineItemConstants.MEANS_OF_TRANSPORTATION_ID, meansOfTransportation)
    configuratorValues.put(lineItemConstants.MODE_OF_TRANSPORTATION_ID, modeOfTransportation)
    configuratorValues.put(lineItemConstants.PRICE_TYPE_ID, priceType)
    configuratorValues.put(lineItemConstants.PRICE_LIST_ID, calculations.getInputValue(lnProduct, lineItemConstants.PRICE_LIST_ID))
    configuratorValues.put(lineItemConstants.COMPETITOR_PRICE_ID, calculations.getInputValue(lnProduct, lineItemConstants.COMPETITOR_PRICE_ID))
    configuratorValues.put(lineItemConstants.PRICE_ID, calculations.getInputValue(lnProduct, lineItemConstants.PRICE_ID))
    configuratorValues.put(lineItemConstants.DELIVERED_PRICE_ID, calculations.getInputValue(lnProduct, lineItemConstants.DELIVERED_PRICE_ID))
    configuratorValues.put(lineItemConstants.PRICING_UOM_ID, calculations.getInputValue(lnProduct, lineItemConstants.PRICING_UOM_ID))
    configuratorValues.put(lineItemConstants.NUMBER_OF_DECIMALS_ID, calculations.getInputValue(lnProduct, lineItemConstants.NUMBER_OF_DECIMALS_ID))
    configuratorValues.put(lineItemConstants.PER_ID, calculations.getInputValue(lnProduct, lineItemConstants.PER_ID))
    configuratorValues.put(lineItemConstants.CURRENCY_ID, calculations.getInputValue(lnProduct, lineItemConstants.CURRENCY_ID))
    configuratorValues.put(lineItemConstants.PRICE_VALID_FROM_ID, calculations.getInputValue(lnProduct, lineItemConstants.PRICE_VALID_FROM_ID))
    configuratorValues.put(lineItemConstants.PRICE_VALID_TO_ID, calculations.getInputValue(lnProduct, lineItemConstants.PRICE_VALID_TO_ID))
    configuratorValues.put(lineItemConstants.FREIGHT_TERM_ID, freightTerm)
    configuratorValues.put(lineItemConstants.INCO_TERM_ID, calculations.getInputValue(lnProduct, lineItemConstants.INCO_TERM_ID))
    if (calculations.getInputValue(lnProduct, lineItemConstants.INCO_TERM_ID)) {
        configuratorValues.put(lineItemConstants.FREIGHT_TERM_HIDDEN_ID, freightTerm)
    }
    configuratorValues.put(lineItemConstants.NAMED_PLACE_ID, calculations.getInputValue(lnProduct, lineItemConstants.NAMED_PLACE_ID))
    configuratorValues.put(lineItemConstants.SALES_PERSON_ID, calculations.getInputValue(lnProduct, lineItemConstants.SALES_PERSON_ID))
    configuratorValues.put(lineItemConstants.REJECTION_REASON_ID, rejectionReason)
    configuratorValues.put(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID))
    configuratorValues.put(lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID, referencePeriod)
    configuratorValues.put(lineItemConstants.PF_CONFIGURATOR_ADDER_ID, calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_ADDER_ID))
    configuratorValues.put(lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID, calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID))
    configuratorValues.put(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID, calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID))
    configuratorValues.put(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID, calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID))
    configuratorValues.put(lineItemConstants.FREIGHT_ESTIMATE_ID, calculations.getInputValue(lnProduct, lineItemConstants.FREIGHT_ESTIMATE_ID))

    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
            "name"    : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME,
            "label"   : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME,
            "type"    : InputType.CONFIGURATOR,
            "url"     : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_URL,
            "readOnly": false,
            "value"   : configuratorValues,
    ])
}

return null