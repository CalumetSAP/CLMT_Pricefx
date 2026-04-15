if (api.global.isFullListRecalc) {
    api.removeManualOverride("NewListPrice")
    api.removeManualOverride("NewJobberDealerPrice")
    api.removeManualOverride("JobberDealerPercent")
    api.removeManualOverride("NewSRP")
    api.removeManualOverride("SRPPercent")
    api.removeManualOverride("NewMapPrice")
    api.removeManualOverride("MAPPercent")
}

def currentContext = api.local.currentContext
checkPriceChangePerUOMAndPercentOverridableInputs(currentContext, "NewListPrice", "ListPriceChangePerUOM", "ListPriceChangePercent")
checkPriceChangePerUOMAndPercentOverridableInputs(currentContext, "NewBasePrice", "BasePriceChangePerUOM", "BasePriceChangePercent")

checkBasePricingColumns(currentContext, "NewJobberDealerPrice", "JobberDealerPercent")
checkBasePricingColumns(currentContext, "NewSRP", "SRPPercent")
checkBasePricingColumns(currentContext, "NewMapPrice", "MAPPercent")

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

def checkBasePricingColumns (currentContext, String priceElementName, percentElementName) {
    def priceOverride = api.getManualOverride(priceElementName)
    if (priceOverride && priceOverride != currentContext?.get(priceElementName)) {
        api.removeManualOverride(percentElementName)
    }

    def percentOverride = api.getManualOverride(percentElementName)
    if (percentOverride && percentOverride != currentContext?.get(percentElementName)) {
        api.removeManualOverride(priceElementName)
    }
}