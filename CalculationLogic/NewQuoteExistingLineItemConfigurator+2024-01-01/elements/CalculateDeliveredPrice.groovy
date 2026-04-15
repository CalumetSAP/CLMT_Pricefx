if (api.isInputGenerationExecution()) return

def priceCompleted = out.PriceCompletedHidden?.getFirstInput()?.getValue()

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def globalUOMConversionMap = api.local.globalUOMTable ?: [:]
def uomConversionMap = api.local.uomTable ?: [:]

// Update Prices
def material = InputMaterial?.input?.getValue()
def price = InputPrice?.entry?.getFirstInput()?.getValue()
def deliveredPrice = InputDeliveredPrice?.entry?.getFirstInput()?.getValue()
def freightAmount = InputFreightAmount?.entry?.getFirstInput()?.getValue() ?: BigDecimal.ZERO
def freightUOM = InputFreightUOM?.input?.getValue()
def pricingUOM = InputPricingUOM?.input?.getValue()
def priceType = InputPriceType?.input?.getValue()

def convertedFreight = freightAmount
if (freightAmount && freightUOM) {
    def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, freightUOM, pricingUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
    if (!conversionFactor) {
        api.local.freightAlert = "Missing 'UOM conversion' from Adder UOM (${freightUOM}) to Pricing UOM (${pricingUOM}) for material ${material}"
        return null
    }
    convertedFreight = freightAmount * conversionFactor
}
api.local.convertedFreight = convertedFreight

if (((priceCompleted == lineItemConstants.PRICE_COMPLETED_PRICE_ID || priceCompleted == lineItemConstants.PRICE_COMPLETED_ADDER_ID) && priceType != "3")) {
    def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"
    def result = price != null ? libs.QuoteLibrary.RoundingUtils.round(price?.toBigDecimal() + convertedFreight?.toBigDecimal(), numberOfDecimals?.toInteger()) : null

    InputDeliveredPrice?.entry?.getFirstInput()?.setValue(result)
    out.HiddenValues?.getInputs()?.find { it.name == InputDeliveredPrice?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(result)
}

if (priceCompleted == lineItemConstants.PRICE_COMPLETED_DELIVERED_PRICE_ID && priceType != "3") {
    def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"
    def result = libs.QuoteLibrary.RoundingUtils.round(deliveredPrice?.toBigDecimal() - convertedFreight?.toBigDecimal(), numberOfDecimals?.toInteger())

    InputPrice?.entry?.getFirstInput()?.setValue(result)
    out.HiddenValues?.getInputs()?.find { it.name == InputPrice?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(result)
}

if (api.local.priceTypeHasChanged && !api.local.shouldUseDefault) {
    InputDeliveredPrice?.entry?.getFirstInput()?.setValue(null)
    out.HiddenValues?.getInputs()?.find { it.name == InputDeliveredPrice?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(null)

    InputPrice?.entry?.getFirstInput()?.setValue(null)
    out.HiddenValues?.getInputs()?.find { it.name == InputPrice?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(null)

    PFAdder?.entry?.getFirstInput()?.setValue(null)
    out.HiddenValues?.getInputs()?.find { it.name == PFAdder?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(null)
}