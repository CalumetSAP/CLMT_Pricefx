import net.pricefx.server.dto.calculation.ContextParameter

if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType?.input?.getValue()
ContextParameter price = InputPrice?.entry?.getFirstInput()
ContextParameter deliveredPrice = InputDeliveredPrice?.entry?.getFirstInput()
ContextParameter adder = PFAdder?.entry?.getFirstInput()
ContextParameter priceCompleted = out.PriceCompletedHidden?.getFirstInput()

if (priceType == "2") {
    adder?.setValue(null)
}

if (priceType == "3") {
    adder?.setReadOnly(false)
    priceCompleted?.setValue(null)
    return null
}

if (priceType == "4") {
    adder?.setReadOnly(false)
    priceCompleted?.setValue(null)
    InputPricingUOM?.input?.setValue(null)
    InputPer?.input?.setValue(null)
    InputNumberOfDecimals?.input?.setValue(null)
    InputCurrency?.input?.setValue(null)
    return null
}

firstTimeConfiguration(api.local.shouldUseDefault, price, deliveredPrice, adder, priceCompleted)

if (api.local.priceTypeHasChanged && !api.local.shouldUseDefault) {
    priceCompleted?.setValue(null)
}

if (priceCompleted?.getValue() == lineItemConstants.PRICE_COMPLETED_PRICE_ID && price.getValue() == null) {
    updateReadOnly(deliveredPrice, adder, false, priceCompleted, null)
    resetValues(deliveredPrice, adder)
    return null
}
if (priceCompleted?.getValue() == lineItemConstants.PRICE_COMPLETED_DELIVERED_PRICE_ID && deliveredPrice.getValue() == null) {
    updateReadOnly(price, adder, false, priceCompleted, null)
    resetValues(price, adder)
    return null
}
if (priceCompleted?.getValue() == lineItemConstants.PRICE_COMPLETED_ADDER_ID && adder.getValue() == null) {
    updateReadOnly(price, deliveredPrice, false, priceCompleted, null)
    resetValues(price, deliveredPrice)
    return null
}

// If Price is Completed
if (priceCompleted?.getValue() == lineItemConstants.PRICE_COMPLETED_PRICE_ID) updateReadOnly(deliveredPrice, adder, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_PRICE_ID)

// If DeliveredPrice is Completed
if (priceCompleted?.getValue() == lineItemConstants.PRICE_COMPLETED_DELIVERED_PRICE_ID) updateReadOnly(price, adder, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_DELIVERED_PRICE_ID)

// If Adder is Completed
if (priceCompleted?.getValue() == lineItemConstants.PRICE_COMPLETED_ADDER_ID) updateReadOnly(price, deliveredPrice, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_ADDER_ID)

if (priceCompleted?.getValue()) return null

// If Price is Completed
if (isCompleted(price, deliveredPrice, adder)) updateReadOnly(deliveredPrice, adder, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_PRICE_ID)

// If DeliveredPrice is Completed
if (isCompleted(deliveredPrice, price, adder)) updateReadOnly(price, adder, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_DELIVERED_PRICE_ID)

// If Adder is Completed
if (isCompleted(adder, price, deliveredPrice)) updateReadOnly(price, deliveredPrice, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_ADDER_ID)

// If Adder is Completed
if (areCompletedAndNotReadOnly(adder, price, deliveredPrice)) updateReadOnly(deliveredPrice, adder, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_PRICE_ID)

return null

def isCompleted(input, otherInput1, otherInput2) {
    if (input?.getValue() != null && otherInput1?.getValue() == null && otherInput2?.getValue() == null) return true
    return false
}

def areCompletedAndNotReadOnly(input, otherInput1, otherInput2) {
    def value = false
    if (input?.getValue() != null && otherInput1?.getValue() != null && otherInput2?.getValue() != null) value = true
    if (value && !input?.getReadOnly() && !otherInput1?.getReadOnly() && !otherInput2?.getReadOnly()) return true
    return false
}

def updateReadOnly(oneInput, otherInput, value, priceCompletedInput, priceCompleted) {
    oneInput?.setReadOnly(value)
    otherInput?.setReadOnly(value)
    priceCompletedInput?.setValue(priceCompleted)
}

def resetValues(oneInput, otherInput) {
    oneInput?.setValue(null)
    otherInput?.setValue(null)
}

def firstTimeConfiguration(isFirstTime, price, deliveredPrice, adder, priceCompleted) {
    if (!isFirstTime) return

    final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

    if (adder.getValue() != null) {
        updateReadOnly(price, deliveredPrice, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_ADDER_ID)
    } else if (price.getValue() != null) {
        updateReadOnly(deliveredPrice, adder, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_PRICE_ID)
    } else if (deliveredPrice.getValue() != null) {
        updateReadOnly(price, adder, true, priceCompleted, lineItemConstants.PRICE_COMPLETED_DELIVERED_PRICE_ID)
    }
}