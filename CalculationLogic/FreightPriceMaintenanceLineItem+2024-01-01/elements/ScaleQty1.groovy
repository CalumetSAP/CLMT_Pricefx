def scaleQty = out.LoadQuoteScales?.getAt(0)?.ScaleQty

return api.attributedResult(scaleQty).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())