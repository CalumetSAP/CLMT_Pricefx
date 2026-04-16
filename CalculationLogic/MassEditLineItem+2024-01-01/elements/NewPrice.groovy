if (!out.NumberOfDecimals) return

BigDecimal newPrice = null
if (out.CurrentPrice?.toBigDecimal()) {
    if (out.IsPriceChangePerUOM) {
        BigDecimal currPrice = out.CurrentPrice?.toBigDecimal()
        BigDecimal priceChange = out.PriceChangePerUOM?.toBigDecimal()
        if (priceChange == BigDecimal.ZERO) {
            newPrice = currPrice
        } else {
            String pricingUOM = out.PricingUOM
            String selectedUOM = out.UOM
            if (priceChange && selectedUOM && pricingUOM && selectedUOM != pricingUOM) {//convert priceChange from selectedUOM to pricingUOM
                String material = api.local.material
                BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, selectedUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
                if (conversionFactor) {
                    priceChange = priceChange * conversionFactor
                } else {
                    api.criticalAlert("Missing conversion from Input UOM (${selectedUOM}) to Price UOM (${pricingUOM}) for material ${material}")
                    priceChange = null
                }
            }
            newPrice = priceChange && currPrice ? currPrice + priceChange : null
        }
    } else {
        BigDecimal currPrice = out.CurrentPrice?.toBigDecimal()
        BigDecimal priceChangePercent = out.PriceChangePercent?.toBigDecimal()

        newPrice = currPrice != null && priceChangePercent != null ? currPrice * (1 + priceChangePercent) : null
    }
}

newPrice = libs.SharedLib.RoundingUtils.round(newPrice, out.NumberOfDecimals)
return api.attributedResult(newPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())
