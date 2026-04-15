def currentContext = api.local.currentContext
checkPriceChangePerUOMAndPercentOverridableInputs(currentContext, "NewListPrice", "ListPriceChangePerUOM", "ListPriceChangePercent")

def checkPriceChangePerUOMAndPercentOverridableInputs (currentContext, String newPriceElementName, String priceChangePerUOMElementName, String priceChangePercentElementName) {
    def newPriceOverride = api.getManualOverride(newPriceElementName)
    if (newPriceOverride && newPriceOverride != currentContext?.get(newPriceElementName)) {
        api.removeManualOverride(priceChangePerUOMElementName)
        api.removeManualOverride(priceChangePercentElementName)
    }

    def priceChangePerUOM = api.getManualOverride(priceChangePerUOMElementName)
    if (priceChangePerUOM && priceChangePerUOM != currentContext?.get(priceChangePerUOMElementName)) {
        api.removeManualOverride(newPriceElementName)
    }

    def priceChangePercentOverride = api.getManualOverride(priceChangePercentElementName)
    if (priceChangePercentOverride && priceChangePercentOverride != currentContext?.get(priceChangePercentElementName)) {
        api.removeManualOverride(newPriceElementName)
    }
}