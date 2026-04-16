BigDecimal newPrice = null
BigDecimal currPrice = out.CurrentListPrice?.toBigDecimal()
BigDecimal priceChangePerUOM = out.ListPriceChangePerUOM?.toBigDecimal()
BigDecimal priceChangePercent = out.ListPriceChangePercent?.toBigDecimal()
String pricingUOM = out.PriceUOM
String selectedUOM = out.ListPriceChangeUOM
Boolean isPriceChangePercent = out.IsPriceChangePercent
Boolean isPriceChangePerUOM = out.IsPriceChangePerUOM
String material = api.local.material

newPrice = libs.PricelistLib.Calculations.getNewPrice(currPrice, isPriceChangePerUOM, isPriceChangePercent, priceChangePerUOM, selectedUOM, pricingUOM, priceChangePercent, material, api.global.uomConversion, api.global.globalUOMConversion)

return api.attributedResult(newPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())