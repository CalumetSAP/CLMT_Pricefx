import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]
def meansOfTransportationOptions = out.FindMeansOfTransportation && !api.isInputGenerationExecution() ? out.FindMeansOfTransportation as Map : [:]
def modeOfTransportationOptions = out.FindModeOfTransportation && !api.isInputGenerationExecution() ? out.FindModeOfTransportation as Map : [:]

api.local.configuratorHasChanged = false
api.local.lineItemChanged = null
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def configuratorValues = lnProduct.inputs.find {
        lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    if (configuratorValues?.get(lineItemConstants.CONFIGURATOR_HAS_CHANGED_ID) == true) {

        configuratorValues.put(lineItemConstants.CONFIGURATOR_HAS_CHANGED_ID, false)

        quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
                "name"    : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME,
                "label"   : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME,
                "type"    : InputType.CONFIGURATOR,
                "url"     : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_URL,
                "readOnly": false,
                "value"   : configuratorValues,
        ])

        api.local.configuratorHasChanged = true
        api.local.lineItemChanged = lnProduct.lineId

        updateValue(lnProduct.lineId, lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID, configuratorValues?.get(lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID))
        updateValue(lnProduct.lineId, lineItemConstants.THIRD_PARTY_CUSTOMER_ID, configuratorValues?.get(lineItemConstants.THIRD_PARTY_CUSTOMER_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PLANT_ID, configuratorValues?.get(lineItemConstants.PLANT_ID))
        updateValue(lnProduct.lineId, lineItemConstants.SHIPPING_POINT_ID, configuratorValues?.get(lineItemConstants.SHIPPING_POINT_ID))
        updateValue(lnProduct.lineId, lineItemConstants.MOQ_ID, configuratorValues?.get(lineItemConstants.MOQ_ID))
        updateValue(lnProduct.lineId, lineItemConstants.MOQ_UOM_ID, configuratorValues?.get(lineItemConstants.MOQ_UOM_ID))
        updateValue(lnProduct.lineId, lineItemConstants.MEANS_OF_TRANSPORTATION_ID, meansOfTransportationOptions?.get(configuratorValues?.get(lineItemConstants.MEANS_OF_TRANSPORTATION_ID)))
        updateValue(lnProduct.lineId, lineItemConstants.MODE_OF_TRANSPORTATION_ID, modeOfTransportationOptions?.get(configuratorValues?.get(lineItemConstants.MODE_OF_TRANSPORTATION_ID)))
        updateValue(lnProduct.lineId, lineItemConstants.PRICE_TYPE_ID, dropdownOptions["PriceType"]?.get(configuratorValues?.get(lineItemConstants.PRICE_TYPE_ID)))
        updateValue(lnProduct.lineId, lineItemConstants.PRICE_LIST_ID, configuratorValues?.get(lineItemConstants.PRICE_LIST_ID))
        updateValue(lnProduct.lineId, lineItemConstants.COMPETITOR_PRICE_ID, configuratorValues?.get(lineItemConstants.COMPETITOR_PRICE_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PRICE_ID, configuratorValues?.get(lineItemConstants.PRICE_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PRICE_ID + "Previous", configuratorValues?.get(lineItemConstants.PRICE_ID))
        updateValue(lnProduct.lineId, lineItemConstants.DELIVERED_PRICE_ID, configuratorValues?.get(lineItemConstants.DELIVERED_PRICE_ID))
        updateValue(lnProduct.lineId, lineItemConstants.DELIVERED_PRICE_ID + "Previous", configuratorValues?.get(lineItemConstants.DELIVERED_PRICE_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PRICING_UOM_ID, configuratorValues?.get(lineItemConstants.PRICING_UOM_ID))
        updateValue(lnProduct.lineId, lineItemConstants.NUMBER_OF_DECIMALS_ID, configuratorValues?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PER_ID, configuratorValues?.get(lineItemConstants.PER_ID))
        updateValue(lnProduct.lineId, lineItemConstants.CURRENCY_ID, configuratorValues?.get(lineItemConstants.CURRENCY_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PRICE_VALID_FROM_ID, configuratorValues?.get(lineItemConstants.PRICE_VALID_FROM_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PRICE_VALID_TO_ID, configuratorValues?.get(lineItemConstants.PRICE_VALID_TO_ID))
        updateValue(lnProduct.lineId, lineItemConstants.FREIGHT_TERM_ID, dropdownOptions["FreightTerm"]?.get(configuratorValues?.get(lineItemConstants.FREIGHT_TERM_ID)))
        updateValue(lnProduct.lineId, lineItemConstants.INCO_TERM_ID, configuratorValues?.get(lineItemConstants.INCO_TERM_ID))
        updateValue(lnProduct.lineId, lineItemConstants.NAMED_PLACE_ID, configuratorValues?.get(lineItemConstants.NAMED_PLACE_ID))
        updateValue(lnProduct.lineId, lineItemConstants.SALES_PERSON_ID, configuratorValues?.get(lineItemConstants.SALES_PERSON_ID))
        updateValue(lnProduct.lineId, lineItemConstants.REJECTION_REASON_ID, dropdownOptions["RejectionReason"]?.get(configuratorValues?.get(lineItemConstants.REJECTION_REASON_ID)))
        updateValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, configuratorValues?.get(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID, dropdownOptions["ReferencePeriod"]?.get(configuratorValues?.get(lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)))
        updateValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_ADDER_ID, configuratorValues?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_ADDER_ID + "Previous", configuratorValues?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID, configuratorValues?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID, configuratorValues?.get(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID))
        updateValue(lnProduct.lineId, lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID, configuratorValues?.get(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID))
        updateValue(lnProduct.lineId, lineItemConstants.FREIGHT_ESTIMATE_ID, configuratorValues?.get(lineItemConstants.FREIGHT_ESTIMATE_ID))
        updateValue(lnProduct.lineId, lineItemConstants.FREIGHT_AMOUNT_ID, configuratorValues?.get(lineItemConstants.FREIGHT_AMOUNT_ID))
        updateValue(lnProduct.lineId, lineItemConstants.FREIGHT_VALID_FROM_ID, configuratorValues?.get(lineItemConstants.FREIGHT_VALID_FROM_ID))
        updateValue(lnProduct.lineId, lineItemConstants.FREIGHT_VALID_TO_ID, configuratorValues?.get(lineItemConstants.FREIGHT_VALID_TO_ID))
        updateValue(lnProduct.lineId, lineItemConstants.FREIGHT_UOM_ID, configuratorValues?.get(lineItemConstants.FREIGHT_UOM_ID))
        updateValue(lnProduct.lineId, lineItemConstants.FREIGHT_PREVIOUS_VALUES_ID, configuratorValues?.get(lineItemConstants.FREIGHT_PREVIOUS_VALUES_ID))

        break
    }
}

return null

def updateValue(String lineId, name, value) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : value,
    ])
}