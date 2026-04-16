BigDecimal newPrice = null
if (out.CurrentBasePrice?.toBigDecimal()) {
    if (out.IsPriceChangePerUOM) {
        BigDecimal currPrice = out.CurrentBasePrice?.toBigDecimal()
        BigDecimal priceChange = out.BasePriceChangePerUOM?.toBigDecimal()
        if (priceChange == BigDecimal.ZERO) {
            newPrice = currPrice
        } else {
            String pricingUOM = out.BaseUOM
            String selectedUOM = out.BasePriceChangeUOM
            if (priceChange && selectedUOM && pricingUOM && selectedUOM != pricingUOM) {//convert priceChange from selectedUOM to pricingUOM
                String material = api.local.material
                BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, selectedUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
                if (conversionFactor) {
                    priceChange = priceChange * conversionFactor
                } else {
                    api.criticalAlert("Missing conversion from Input UOM (${selectedUOM}) to ZBPL UOM (${pricingUOM}) for material ${material}")
                    priceChange = null
                }
            }
            newPrice = priceChange && currPrice ? currPrice + priceChange : null
        }
    } else if (out.IsPriceChangePercent) {
        BigDecimal currPrice = out.CurrentBasePrice?.toBigDecimal()
        BigDecimal priceChangePercent = out.BasePriceChangePercent?.toBigDecimal()

        newPrice = currPrice != null && priceChangePercent != null ? currPrice * (1 + priceChangePercent) : null
    }
}

return api.attributedResult(newPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())
