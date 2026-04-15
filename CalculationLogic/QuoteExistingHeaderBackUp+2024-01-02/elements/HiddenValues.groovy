if (api.isInputGenerationExecution() || !quoteProcessor.isPrePhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]

def priceHasChanged = [:]
def deliveredPriceHasChanged = [:]
def adderHasChanged = [:]

def priceType, price, prevPrice, deliveredPrice, prevDeliveredPrice, adder, prevAdder
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    priceType = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
    priceType = priceType ? dropdownOptions["PriceType"]?.find { k, v -> v.toString().startsWith(priceType as String) }?.key : priceType

    price = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_ID)
    prevPrice = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_ID + "Previous")
    deliveredPrice = calculations.getInputValue(lnProduct, lineItemConstants.DELIVERED_PRICE_ID)
    prevDeliveredPrice = calculations.getInputValue(lnProduct, lineItemConstants.DELIVERED_PRICE_ID + "Previous")
    adder = calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_ADDER_ID)
    prevAdder = calculations.getInputValue(lnProduct, lineItemConstants.PF_CONFIGURATOR_ADDER_ID + "Previous")

    if (price != prevPrice) priceHasChanged.put(lnProduct.lineId, true)
    if (deliveredPrice != prevDeliveredPrice) deliveredPriceHasChanged.put(lnProduct.lineId, true)
    if (adder != prevAdder) adderHasChanged.put(lnProduct.lineId, true)

    if (priceType == "1") {
        if (price && (!deliveredPrice || !adder)) priceHasChanged.put(lnProduct.lineId, true)
        if (deliveredPrice && (!price || !adder)) deliveredPriceHasChanged.put(lnProduct.lineId, true)
        if (adder && (!price || !deliveredPrice)) adderHasChanged.put(lnProduct.lineId, true)
    }
    if (priceType == "2") {
        if (price && !deliveredPrice) priceHasChanged.put(lnProduct.lineId, true)
        if (deliveredPrice && !price) deliveredPriceHasChanged.put(lnProduct.lineId, true)
    }

}

api.local.priceHasChanged = priceHasChanged
api.local.deliveredPriceHasChanged = deliveredPriceHasChanged
api.local.adderHasChanged = adderHasChanged

return null