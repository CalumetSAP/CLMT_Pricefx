def priceChangePerUOM = api.global.priceChangePerUOM

def newPriceOverride = api.getManualOverride("NewBasePrice")
def currentPrice = out.CurrentBasePrice
//This only applies if the user overrides the New Base Price
if (newPriceOverride != null && currentPrice != null) {
    priceChangePerUOM = newPriceOverride - currentPrice
    String priceUOM = out.BaseUOM
    String inputUOM = api.global.uom
    if (inputUOM && priceUOM && inputUOM != priceUOM) {
        String material = api.local.material
        BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, priceUOM, inputUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
        if (conversionFactor) {
            priceChangePerUOM = priceChangePerUOM * conversionFactor
        } else {
            api.criticalAlert("Missing conversion from ZBPL UOM (${priceUOM}) to Input UOM (${inputUOM}) for material ${material}")
            priceChangePerUOM = null
        }
    }
}

return api.attributedResult(priceChangePerUOM).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())