List<String> errors = []

def per = out.Per
def zbplUom = out.BaseUOM
def newEffectiveDate = out.NewEffectiveDate
def newExpirationDate = out.NewExpirationDate
def price = out.NewBasePrice

if(!per || per <= 0) {
    errors.add("Per is empty")
}

if(!zbplUom) {
    errors.add("ZBPL UOM is empty")
}

if(!newEffectiveDate) {
    errors.add("New Effective Date is empty")
}

if(!newExpirationDate) {
    errors.add("New Expiration Date is empty")
}

if(!price) {
    errors.add("Price is empty")
}

Integer maxNumberOfDecimals = api.global.maxNumberOfDecimals
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