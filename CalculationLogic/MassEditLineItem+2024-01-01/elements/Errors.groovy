List<String> errors = []

Integer maxNumberOfDecimals = out.NumberOfDecimals?.toInteger()

if (!maxNumberOfDecimals) {
    errors.add("Number of Decimals is missing")
}

BigDecimal newPrice = out.NewPrice?.toBigDecimal()
if (!newPrice || newPrice <= BigDecimal.ZERO) {
    errors.add("'New Price' should be greater than zero")
}

def newPriceOverride = api.getManualOverride("NewPrice")
if (newPriceOverride && maxNumberOfDecimals) {
    List<String> newPriceSplitted = newPriceOverride.toString().split("\\.")
    if (newPriceSplitted.size() > 1) {
        String newPriceDecimals = newPriceSplitted[1]
        if (newPriceDecimals && (newPriceDecimals as Integer) && newPriceDecimals.length() > maxNumberOfDecimals) {
            errors.add("'New Price' should not have more than ${maxNumberOfDecimals} decimals")
        }
    }
}

List<String> missingScaleQtys = []
List<String> missingScalePrices = []
List<String> scalePricesWithWrongDecimals = []
def scaleQty, scalePrice
List scaleQtys = []
List<String> scalePriceSplitted
String scalePriceDecimals
for (int i = 1; i < 6; i++) {
    scaleQty = out["ScaleQty${i}"]
    scalePrice = out["Price${i}"]
    if (scaleQty) {
        scaleQtys.add(scaleQty.toDouble())
        // Scale Qty and Price are both required if one is filled
        if (scalePrice == null) {
            missingScalePrices.add("'Price ${i}'")
        }
    }
    if (scalePrice) {
        // Scale Qty and Price are both required if one is filled
        if (scaleQty == null) {
            missingScaleQtys.add("'Scale Qty ${i}'")
        }
        // Price number of decimals can't be greater than 2
        scalePriceSplitted = scalePrice.toString().split("\\.")
        if (scalePriceSplitted.size() > 1) {
            scalePriceDecimals = scalePriceSplitted[1]
            if (scalePriceDecimals && (scalePriceDecimals as Integer) && scalePriceDecimals.length() > maxNumberOfDecimals) {
                scalePricesWithWrongDecimals.add("'Price ${i}'")
            }
        }
    }
}
if (!scaleQtys.isEmpty() && !out.ScaleUOM) {
    errors.add("Scale UOM is empty")
}
if (!missingScaleQtys.isEmpty()) {
    errors.add("${missingScaleQtys.join(", ")} missing")
}
if (!missingScalePrices.isEmpty()) {
    errors.add("${missingScalePrices.join(", ")} missing")
}
// Scale Qty can't be duplicated
List duplicatedScaleQtys = scaleQtys.countBy { it }.findAll { it.value > 1 }.collect { it.key }
if (!duplicatedScaleQtys.isEmpty()) {
    errors.add("There are duplicated 'Scale Quantities': ${duplicatedScaleQtys.join(", ")}")
}
if (!scalePricesWithWrongDecimals.isEmpty()) {
    errors.add("${scalePricesWithWrongDecimals.join(", ")} should not have more than ${maxNumberOfDecimals} decimals")
}


if (errors) {
    String errorMsg = errors.join("; ")
    api.criticalAlert(errorMsg)
    return errorMsg
}

return null