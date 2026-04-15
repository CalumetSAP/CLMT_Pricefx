def priceChangePercent = api.global.priceChangePercent

//TODO move to a PricelistLib. DUPLICATED (2)
def newPriceOverride = api.getManualOverride("NewListPrice")
def currentPrice = out.CurrentListPrice
//This only applies if the user overrides the New List Price
if (newPriceOverride != null && currentPrice) {
    priceChangePercent = (newPriceOverride/currentPrice) - 1
}

return api.attributedResult(priceChangePercent).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())