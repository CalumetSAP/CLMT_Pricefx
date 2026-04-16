def priceChangePerUOM = api.global.priceChangePerUOM
def newPriceOverride = api.getManualOverride("NewPrice")
def currentPrice = out.CurrentPrice
if (newPriceOverride != null && currentPrice != null) {
    priceChangePerUOM = newPriceOverride - currentPrice
    String selectedUOM = api.global.uom
    String pricingUOM = out.PricingUOM
    if (selectedUOM && pricingUOM && selectedUOM != pricingUOM) {
        String material = api.local.material
        BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, selectedUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
        if (conversionFactor) {
            priceChangePerUOM = priceChangePerUOM * conversionFactor
        } else {
            api.criticalAlert("Missing conversion from Price UOM (${pricingUOM}) to Input UOM (${selectedUOM}) for material ${material}")
            priceChangePerUOM = null
        }
    }
}

api.attributedResult(priceChangePerUOM).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())