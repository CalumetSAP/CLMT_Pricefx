def scaleQty = out.LoadQuoteScales?.getAt(1)?.ScaleQty

return api.attributedResult(scaleQty).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())