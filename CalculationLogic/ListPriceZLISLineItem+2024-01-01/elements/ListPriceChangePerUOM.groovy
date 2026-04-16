def priceChangePerUOM = api.global.priceChangePerUOM

//TODO move to a PricelistLib. DUPLICATED (1)
def newPriceOverride = api.getManualOverride("NewListPrice")
def currentPrice = out.CurrentListPrice
//This only applies if the user overrides the New List Price
if (newPriceOverride != null && currentPrice != null) {
    priceChangePerUOM = newPriceOverride - currentPrice
    String priceUOM = out.PriceUOM
    String inputUOM = api.global.uom
    if (inputUOM && priceUOM && inputUOM != priceUOM) {
        String material = api.local.material
        BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, priceUOM, inputUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
        if (conversionFactor) {
            priceChangePerUOM = priceChangePerUOM * conversionFactor
        } else {
            api.criticalAlert("Missing conversion from ZLIS UOM (${priceUOM}) to Input UOM (${inputUOM}) for material ${material}")
            priceChangePerUOM = null
        }
    }
}

return api.attributedResult(priceChangePerUOM).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())