if (out.CondType != "ZPFX") api.removeManualOverride("NewAdder")

final rounding = libs.SharedLib.RoundingUtils
def freightConditionTypes = [
        "1": "ZFDD",
        "2": "ZFDD",
        "3": "ZFDL",
        "4": "ZFDL",
]

def freightChangeAmount = out.FreightChangeAmount
def adder = out.OldAdder
def productPrice = out.OldProductPrice
def freightAmount = out.OldFreightAmount
def deliveredPrice = out.OldDeliveredPrice

def numberOfDecimals = out.NumberOfDecimals

if (out.HaulCharges == null) return returnValues(adder, productPrice, null, deliveredPrice)

if (freightChangeAmount == BigDecimal.ZERO) return returnValues(adder, productPrice, freightAmount, deliveredPrice)

def freightCondition = freightConditionTypes[out.FreightTerm]
def freightChange = freightChangeAmount > 0 ? "Increase" : "Decrease"
def conditionType = out.CondType

def groundRulesMap = api.global.groundRules as Map

def freightCondMap = groundRulesMap[freightCondition] ?: groundRulesMap["*"]
if (!freightCondMap) return returnValues(adder, productPrice, freightAmount, deliveredPrice)

def freightChangeMap = freightCondMap[freightChange]
if (!freightChangeMap) return returnValues(adder, productPrice, freightAmount, deliveredPrice)

def conditionTypeMap = freightChangeMap[conditionType] ?: freightChangeMap["*"]
if (!conditionTypeMap) return returnValues(adder, productPrice, freightAmount, deliveredPrice)

def adderProtection = out.FixedAdder ?: "N"
def rules = conditionTypeMap[adderProtection] ?: conditionTypeMap["*"]
if (!rules) return returnValues(adder, productPrice, freightAmount, deliveredPrice)

def maintainDeliveredPrice = rules.DeliveredPrice == "Maintain"
def changeAdder = rules.AdjustmentFactor.toUpperCase().contains("ADDER")
def changeProductPrice = rules.AdjustmentFactor.toUpperCase().contains("PRODUCT PRICE")

def conversionFromFreightUOMToPricingUOM = libs.QuoteLibrary.Conversion.getConversionFactor(api.local.sku, out.FreightUOM, out.ProductUOM,
        api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()

if (!conversionFromFreightUOMToPricingUOM) {
    api.local.conversionAlerts.add("Missing 'UOM conversion' from Freight UOM (${out.FreightUOM}) to Product UOM (${out.ProductUOM}) for material ${api.local.sku}")
    return returnValues(adder, productPrice, freightAmount, deliveredPrice)
}

if (maintainDeliveredPrice && changeAdder) {
    def conversionFromPricingUOMToAdderUOM = libs.QuoteLibrary.Conversion.getConversionFactor(api.local.sku, out.ProductUOM, out.AdderUOM,
            api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
    if (!conversionFromPricingUOMToAdderUOM) {
        api.local.conversionAlerts.add("Missing 'UOM conversion' from Product UOM (${out.ProductUOM}) to Adder UOM (${out.AdderUOM}) for material ${api.local.sku}")
        return returnValues(adder, productPrice, freightAmount, deliveredPrice)
    }

    def convertedPrice = rounding.round(productPrice * conversionFromPricingUOMToAdderUOM, numberOfDecimals)
    def indexValue = convertedPrice - adder

    def newFreight = out.FlatRatePerFreightUOM ?: BigDecimal.ZERO
    def convertedNewFreight = rounding.round(newFreight * conversionFromFreightUOMToPricingUOM, 2)

    def newProductPrice = deliveredPrice - convertedNewFreight
    def newConvertedPrice = rounding.round(newProductPrice * conversionFromPricingUOMToAdderUOM, numberOfDecimals)
    def newAdder = newConvertedPrice - indexValue

    return returnValues(newAdder, newProductPrice, newFreight, deliveredPrice)
} else if (maintainDeliveredPrice && changeProductPrice) {
    def newFreight = out.FlatRatePerFreightUOM ?: BigDecimal.ZERO
    def convertedNewFreight = rounding.round(newFreight * conversionFromFreightUOMToPricingUOM, 2)

    def newProductPrice = deliveredPrice - convertedNewFreight

    return returnValues(adder, newProductPrice, newFreight, deliveredPrice)
} else {
    def newFreight = out.FlatRatePerFreightUOM ?: BigDecimal.ZERO
    def convertedNewFreight = rounding.round(newFreight * conversionFromFreightUOMToPricingUOM, 2)
    def newDeliveredPrice = freightCondition == "ZFDL" ? out.OldProductPrice + convertedNewFreight : out.OldProductPrice

    return returnValues(adder, productPrice, newFreight, newDeliveredPrice)
}

return returnValues(adder, productPrice, freightAmount, deliveredPrice)

def returnValues(adder, productPrice, freightAmount, deliveredPrice) {
    return [
            NewAdder         : adder,
            NewProductPrice  : productPrice,
            NewFreightAmount : freightAmount,
            NewDeliveredPrice: deliveredPrice,
    ]
}