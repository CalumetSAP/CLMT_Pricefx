def price = out.LoadQuoteScales?.getAt(1)?.Price

return api.attributedResult(price).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())