def scaleQty = out.LoadQuoteScales?.getAt(4)?.ScaleQty

return api.attributedResult(scaleQty).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())