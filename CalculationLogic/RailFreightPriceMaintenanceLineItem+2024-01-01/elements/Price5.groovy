def price = out.LoadQuoteScales?.getAt(4)?.Price

return api.attributedResult(price).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())