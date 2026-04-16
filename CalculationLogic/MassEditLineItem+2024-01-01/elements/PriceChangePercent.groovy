def priceChangePercent = api.global.priceChangePercent
def newPriceOverride = api.getManualOverride("NewPrice")
def currentPrice = out.CurrentPrice
if (newPriceOverride != null && currentPrice) {
    priceChangePercent = (newPriceOverride/currentPrice) - 1
}

api.attributedResult(priceChangePercent).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())