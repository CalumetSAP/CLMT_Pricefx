def price = out.LoadQuoteScales?.getAt(2)?.Price

return api.attributedResult(price).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())