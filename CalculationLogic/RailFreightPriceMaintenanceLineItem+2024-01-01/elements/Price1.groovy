def price = out.LoadQuoteScales?.getAt(0)?.Price

return api.attributedResult(price).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())