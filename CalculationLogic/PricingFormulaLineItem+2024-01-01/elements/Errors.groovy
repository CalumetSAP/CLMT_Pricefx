List<String> errors = []

if (!out.NumberOfDecimals) {
    errors.add("Number of Decimals is missing")
}

BigDecimal newPrice = out.NewPrice?.toBigDecimal()
if (!newPrice || newPrice <= BigDecimal.ZERO) {
    errors.add("'New Price' should be greater than zero")
}

def newPriceOverride = api.getManualOverride("NewPrice")
if (newPriceOverride && out.NumberOfDecimals) {
    List<String> newPriceSplitted = newPriceOverride.toString().split("\\.")
    if (newPriceSplitted.size() > 1) {
        String newPriceDecimals = newPriceSplitted[1]
        if (newPriceDecimals && (newPriceDecimals as Integer) && newPriceDecimals.length() > (out.NumberOfDecimals as Integer)) {
            errors.add("'New Price' should not have more than ${out.NumberOfDecimals} decimals")
        }
    }
}

def adderOverride = api.getManualOverride("Adder")
String adderUOM = api.local.adderUOM
if (adderOverride) {
    List<String> adderSplitted = adderOverride.toString().split("\\.")
    if (adderSplitted.size() > 1) {
        String adderDecimals = adderSplitted[1]
//        Integer maxAdderDecimalsAllowed = libs.PricelistLib.Constants.getUOMRoundingDecimals(adderUOM)
        Integer maxAdderDecimalsAllowed = out.NumberOfDecimals as Integer
        if (adderDecimals && (adderDecimals as Integer) && adderDecimals.length() > maxAdderDecimalsAllowed) {
            errors.add("'Adder' should not have more than ${maxAdderDecimalsAllowed} decimals for ${adderUOM} UOM")
        }
    }
}

BigDecimal totalIndexPercent = BigDecimal.ZERO
if (out.Index1) {
    totalIndexPercent += out.Index1Percent ?: BigDecimal.ZERO
}
if (out.Index2) {
    totalIndexPercent += out.Index2Percent ?: BigDecimal.ZERO
}
if (out.Index3) {
    totalIndexPercent += out.Index3Percent ?: BigDecimal.ZERO
}
totalIndexPercent = libs.QuoteLibrary.RoundingUtils.round(totalIndexPercent, 2)
if (totalIndexPercent != BigDecimal.ONE) {
    errors.add("Indices percentages sum should be equal to 100%")
}

if (errors) {
    String errorMsg = errors.join("; ")
    api.criticalAlert(errorMsg)
    return errorMsg
}

return null