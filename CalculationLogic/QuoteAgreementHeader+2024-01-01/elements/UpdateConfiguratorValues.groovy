import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def inputs
if (api.isUserInGroup("Pricing", api.local.loginName) || api.isUserInGroup("Freight", api.local.loginName)) {
    inputs = api.local.pricingAndFreightInputs ?: [:]
} else if (api.isUserInGroup("Sales", api.local.loginName)) {
    inputs = api.local.salesInputs ?: [:]
}

if (!inputs.containsKey(lineItemConstants.CONFIGURATOR_NAME)) return

final calculations = libs.QuoteLibrary.Calculations

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String

    // Update Configurator values
    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.CONFIGURATOR_NAME)
    def shipToValue = configurator?.get(lineItemConstants.SHIP_TO_ID)
    def priceTypeValue = configurator?.get(lineItemConstants.PRICE_TYPE_ID)
    def pricelistValue = configurator?.get(lineItemConstants.PRICE_LIST_ID)
    def plantValue = configurator?.get(lineItemConstants.PLANT_ID)
    def shippingPointValue = configurator?.get(lineItemConstants.SHIPPING_POINT_ID)
    def freightEstimateConfigValue = configurator?.get(lineItemConstants.FREIGHT_ESTIMATE_ID)
    def priceValue = configurator?.get(lineItemConstants.PRICE_ID)
    def perValue = configurator?.get(lineItemConstants.PER_ID)
    def pricingUOMValue = configurator?.get(lineItemConstants.PRICING_UOM_ID)
    def priceValidFromValue = configurator?.get(lineItemConstants.PRICE_VALID_FROM_ID)
    def priceValidToValue = configurator?.get(lineItemConstants.PRICE_VALID_TO_ID)
    def currencyValue = configurator?.get(lineItemConstants.CURRENCY_ID)
    def numberOfDecimalsValue = configurator?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID)

    updateValue(lineId, lineItemConstants.SHIP_TO_ID, shipToValue, null)
    updateValue(lineId, lineItemConstants.PRICE_TYPE_ID, priceTypeValue, null)
    updateValue(lineId, lineItemConstants.PRICE_LIST_ID, pricelistValue, null)
    updateValue(lineId, lineItemConstants.PLANT_ID, plantValue, null)
    updateValue(lineId, lineItemConstants.SHIPPING_POINT_ID, shippingPointValue, null)
    updateValue(lineId, lineItemConstants.FREIGHT_ESTIMATE_ID, freightEstimateConfigValue, null)
    updateValue(lineId, lineItemConstants.PRICE_ID, priceValue, null)
    updateValue(lineId, lineItemConstants.PER_ID, perValue, null)
    updateValue(lineId, lineItemConstants.PRICING_UOM_ID, pricingUOMValue, null)
    updateValue(lineId, lineItemConstants.PRICE_VALID_FROM_ID, priceValidFromValue, null)
    updateValue(lineId, lineItemConstants.PRICE_VALID_TO_ID, priceValidToValue, null)
    updateValue(lineId, lineItemConstants.CURRENCY_ID, currencyValue, null)
    updateValue(lineId, lineItemConstants.NUMBER_OF_DECIMALS_ID, numberOfDecimalsValue, null)

    // Pricing Formula Configurator Values
    def values = [:]
    values.put(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID, configurator?.get(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID))
    values.put(lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID, configurator?.get(lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID))
    values.put(lineItemConstants.PF_CONFIGURATOR_ADDER_ID, configurator?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_ID))
    values.put(lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID, configurator?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID))
    values.put(lineItemConstants.PF_CONFIGURATOR_ADDER_NUMBER_OF_DECIMAL_PLACES_ID, configurator?.get(lineItemConstants.PF_CONFIGURATOR_ADDER_NUMBER_OF_DECIMAL_PLACES_ID))
    values.put(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID, configurator?.get(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID))
    values.put(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID, configurator?.get(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID))

    def previousValues = lnProduct.inputs.find {
        lineItemConstants.PRICING_FORMULA_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
            "name"    : lineItemConstants.PRICING_FORMULA_CONFIGURATOR_NAME,
            "label"   : lineItemConstants.PRICING_FORMULA_CONFIGURATOR_NAME,
            "type"    : InputType.CONFIGURATOR,
            "url"     : lineItemConstants.PRICING_FORMULA_CONFIGURATOR_URL,
            "readOnly": false,
            "value"   : previousValues + values,
    ])

    // Price Protection Configurator Values
//    values = [:]
//    values.put(lineItemConstants.PP_CONFIGURATOR_PRICE_PROTECTION_ID, configurator?.get(lineItemConstants.PP_CONFIGURATOR_PRICE_PROTECTION_ID))
//    values.put(lineItemConstants.PP_CONFIGURATOR_NUMBER_OF_DAYS_ID, configurator?.get(lineItemConstants.PP_CONFIGURATOR_NUMBER_OF_DAYS_ID))
//    values.put(lineItemConstants.PP_CONFIGURATOR_MOVEMENT_TIMING_ID, configurator?.get(lineItemConstants.PP_CONFIGURATOR_MOVEMENT_TIMING_ID))
//    values.put(lineItemConstants.PP_CONFIGURATOR_MOVEMENT_START_ID, configurator?.get(lineItemConstants.PP_CONFIGURATOR_MOVEMENT_START_ID))
//    values.put(lineItemConstants.PP_CONFIGURATOR_MOVEMENT_DAY_ID, configurator?.get(lineItemConstants.PP_CONFIGURATOR_MOVEMENT_DAY_ID))
//
//    previousValues = lnProduct.inputs.find {
//        lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
//    }?.value ?: [:]
//
//    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
//            "name"    : lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_NAME,
//            "label"   : lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_NAME,
//            "type"    : InputType.CONFIGURATOR,
//            "url"     : lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_URL,
//            "readOnly": false,
//            "value"   : previousValues + values,
//    ])

}

return null

def updateValue(String lineId, name, defaultValue, previousValue) {
    if (previousValue) return
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name" : name,
            "value": defaultValue,
    ])
}