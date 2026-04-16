def scaleQty = out.LoadQuoteScales?.getAt(3)?.ScaleQty

return api.attributedResult(scaleQty).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())