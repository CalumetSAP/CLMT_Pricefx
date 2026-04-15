def newPrice = libs.PricelistLib.Calculations.getNewPrice(
        out.LoadQuoteScales?.getAt(0)?.Price,
        out.IsPriceChangePerUOM,
        out.IsPriceChangePercent,
        out.PriceChangePerUOM?.toBigDecimal(),
        out.UOM,
        out.PricingUOM,
        api.global.priceChangePercent,
        api.local.material,
        api.global.uomConversion,
        api.global.globalUOMConversion,
        out.NumberOfDecimals
)

return api.attributedResult(newPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())