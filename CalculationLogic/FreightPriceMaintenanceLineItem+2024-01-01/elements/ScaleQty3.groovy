def scaleQty = out.LoadQuoteScales?.getAt(2)?.ScaleQty

return api.attributedResult(scaleQty).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())