def price = out.LoadQuoteScales?.getAt(3)?.Price

return api.attributedResult(price).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())