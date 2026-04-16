def scaleQty = out.LoadQuoteScales?.getAt(2)?.ScaleQty
scaleQty = out.NumberOfDecimals ? libs.SharedLib.RoundingUtils.round(scaleQty, out.NumberOfDecimals) : scaleQty

return api.attributedResult(scaleQty).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())