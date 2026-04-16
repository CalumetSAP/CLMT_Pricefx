if (out.LoadQuotes && out.LoadExclusions?.attribute2) {
    api.removeManualOverride("EffectiveDate")
    api.removeManualOverride("ExpirationDate")
}

def currentContext = api.currentContext(api.local.material, api.local.secondaryKey)

def newPriceOverride = api.getManualOverride("NewPrice")
if (newPriceOverride && newPriceOverride != currentContext?.get("NewPrice")) {
    api.removeManualOverride("PriceChangePerUOM")
    api.removeManualOverride("PriceChangePercent")
}

def priceChangePerUOM = api.getManualOverride("PriceChangePerUOM")
if (priceChangePerUOM && priceChangePerUOM != currentContext?.get("PriceChangePerUOM")) {
    api.removeManualOverride("NewPrice")
}

def priceChangePercentOverride = api.getManualOverride("PriceChangePercent")
if (priceChangePercentOverride && priceChangePercentOverride != currentContext?.get("PriceChangePercent")) {
    api.removeManualOverride("NewPrice")
}