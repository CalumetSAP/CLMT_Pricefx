def newPrice = libs.PricelistLib.Calculations.getNewPrice(
        out.ZBPLMerged.Scales?.getAt(2)?.ConditionRate,
        out.IsPriceChangePerUOM,
        out.IsPriceChangePercent,
        out.BasePriceChangePerUOM?.toBigDecimal(),
        out.BasePriceChangeUOM,
        out.BaseUOM,
        api.global.priceChangePercent,
        api.local.material,
        api.global.uomConversion,
        api.global.globalUOMConversion,
        api.global.maxNumberOfDecimals
)

return api.attributedResult(newPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())