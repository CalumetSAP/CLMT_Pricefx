import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution() || !quoteProcessor.isPrePhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def globalUOMConversionMap = api.local.globalUOMTable ?: [:]
def uomConversionMap = api.local.uomTable ?: [:]
def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]
def freightValuesFromConfigurator = api.local.freightValuesFromConfigurator
def freightValues = out.FindFreightValues

def material, useConfiguratorValue, configurator, price, deliveredPrice, dsData, freightData, freightAmount, freightUOM, pricingUOM, priceType, priceTypeAux, convertedFreight, numberOfDecimals
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    useConfiguratorValue = api.local.lineItemChanged == lnProduct.lineId
    configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)

    material = lnProduct.sku
    price = findValue(useConfiguratorValue, lineItemConstants.PRICE_ID, lnProduct, configurator)
    deliveredPrice = findValue(useConfiguratorValue, lineItemConstants.DELIVERED_PRICE_ID, lnProduct, configurator)
    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    freightData = freightValuesFromConfigurator?.get(material) ?: freightValues?.get(dsData.SAPContractNumber + "|" + dsData.SAPLineId)
    freightAmount = freightData?.FreightAmount ?: BigDecimal.ZERO
    freightUOM = freightData?.FreightUOM
    pricingUOM = findValue(useConfiguratorValue, lineItemConstants.PRICING_UOM_ID, lnProduct, configurator)
    priceTypeAux = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
    priceType = useConfiguratorValue
            ? configurator?.get(lineItemConstants.PRICE_TYPE_ID)
            : priceTypeAux ? dropdownOptions["PriceType"]?.find { k, v -> v.toString().startsWith(priceTypeAux) }?.key : priceTypeAux
    numberOfDecimals = findValue(useConfiguratorValue, lineItemConstants.NUMBER_OF_DECIMALS_ID, lnProduct, configurator) ?: "2"

    convertedFreight = freightAmount
    if (freightAmount && freightUOM) {
        def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightUOM, pricingUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
        if (!conversionFactor) {
            api.local.alerts = "Missing 'UOM conversion' from Adder UOM (${freightUOM}) to Pricing UOM (${pricingUOM}) for material ${material}"
            continue
        }
        convertedFreight = freightAmount * conversionFactor
    }

    if (api.local.deliveredPriceHasChanged?.get(lnProduct.lineId) && priceType != "3") {
        def result = libs.QuoteLibrary.RoundingUtils.round(deliveredPrice?.toBigDecimal() - convertedFreight?.toBigDecimal(), numberOfDecimals?.toInteger())

        updateValue(lnProduct.lineId, lineItemConstants.PRICE_ID, result)
        updateHiddenValue(lnProduct.lineId, lineItemConstants.PRICE_ID + "Previous", result)

        updateHiddenValue(lnProduct.lineId, lineItemConstants.DELIVERED_PRICE_ID + "Previous", deliveredPrice)
    } else {
        def result = price != null ? libs.QuoteLibrary.RoundingUtils.round(price?.toBigDecimal() + convertedFreight?.toBigDecimal(), numberOfDecimals?.toInteger()) : null

        updateValue(lnProduct.lineId, lineItemConstants.DELIVERED_PRICE_ID, result)
        updateHiddenValue(lnProduct.lineId, lineItemConstants.DELIVERED_PRICE_ID + "Previous", result)

        updateHiddenValue(lnProduct.lineId, lineItemConstants.PRICE_ID + "Previous", price)
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

def updateHiddenValue(String lineId, name, value) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : value,
            "type"        : InputType.HIDDEN,
    ])
}

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}