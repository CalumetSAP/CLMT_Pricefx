api.local.manualOverrideWarnings = []

def overriddenValue = api.currentItem("OverriddenValue")

if (overriddenValue == "Adder" && !api.getManualOverride("NewAdder")) return [OverridenValue: null]
if (overriddenValue == "ProductPrice" && !api.getManualOverride("NewProductPrice")) return [OverridenValue: null]
if (overriddenValue == "DeliveredPrice" && !api.getManualOverride("NewDeliveredPrice")) return [OverridenValue: null]

if (api.getManualOverride("NewAdder") && api.getManualOverride("NewProductPrice")) overriddenValue = "ProductPrice"

if (!overriddenValue) {
    if (api.getManualOverride("NewAdder")) overriddenValue = "Adder"
    if (api.getManualOverride("NewProductPrice")) overriddenValue = "ProductPrice"
    if (api.getManualOverride("NewDeliveredPrice")) overriddenValue = "DeliveredPrice"

    if (!overriddenValue && !api.getManualOverride("NewFreightAmount")) return [:]
}

final rounding = libs.SharedLib.RoundingUtils
def freightConditionTypes = [
        "1": "ZFDD",
        "2": "ZFDD",
        "3": "ZFDL",
        "4": "ZFDL",
]

def freightAmount = api.getManualOverride("NewFreightAmount") ?: (out.FlatRatePerFreightUOM ?: BigDecimal.ZERO)
freightAmount = rounding.round(freightAmount, 2)
def oldAdder = out.OldAdder
def oldProductPrice = out.OldProductPrice
def shouldAddFreight = freightConditionTypes[out.FreightTerm] == "ZFDL"

def numberOfDecimals = out.NumberOfDecimals

def conversionFromPricingUOMToAdderUOM = libs.QuoteLibrary.Conversion.getConversionFactor(api.local.sku, out.ProductUOM, out.AdderUOM,
        api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
if (!conversionFromPricingUOMToAdderUOM && oldAdder) {
    api.local.conversionAlerts.add("Missing 'UOM conversion' from Product UOM (${out.ProductUOM}) to Adder UOM (${out.AdderUOM}) for material ${api.local.sku}")
    return [OverridenValue: overriddenValue]
}
def conversionFromAdderUOMToPricingUOM = libs.QuoteLibrary.Conversion.getConversionFactor(api.local.sku, out.AdderUOM, out.ProductUOM,
        api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
if (!conversionFromAdderUOMToPricingUOM && oldAdder) {
    api.local.conversionAlerts.add("Missing 'UOM conversion' from Adder UOM (${out.AdderUOM}) to Product UOM (${out.ProductUOM}) for material ${api.local.sku}")
    return [OverridenValue: overriddenValue]
}
def conversionFromFreightUOMToPricingUOM = libs.QuoteLibrary.Conversion.getConversionFactor(api.local.sku, out.FreightUOM, out.ProductUOM,
        api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
if (!conversionFromFreightUOMToPricingUOM && shouldAddFreight) {
    api.local.conversionAlerts.add("Missing 'UOM conversion' from Freight UOM (${out.FreightUOM}) to Product UOM (${out.ProductUOM}) for material ${api.local.sku}")
    return [OverridenValue: overriddenValue]
}

def indexValue
if (oldAdder) {
    def convertedPrice = oldProductPrice * conversionFromPricingUOMToAdderUOM
    indexValue = convertedPrice - oldAdder
}

def adder, productPrice, deliveredPrice
if (overriddenValue == "Adder") {
    api.local.manualOverrideWarnings.add("New Adder was manually changed, New Delivered Price cannot be changed manually")

    api.removeManualOverride("NewProductPrice")
    api.removeManualOverride("NewDeliveredPrice")

    adder = api.getManualOverride("NewAdder")

    productPrice = rounding.round((indexValue + adder) * conversionFromAdderUOMToPricingUOM, numberOfDecimals)
    deliveredPrice = shouldAddFreight ? productPrice + (freightAmount * conversionFromFreightUOMToPricingUOM) : productPrice
} else if (overriddenValue == "ProductPrice") {
    api.local.manualOverrideWarnings.add("New Product Price was manually changed, New Adder and New Delivered Price cannot be changed manually")

    api.removeManualOverride("NewAdder")
    api.removeManualOverride("NewDeliveredPrice")

    productPrice = rounding.round(api.getManualOverride("NewProductPrice"), numberOfDecimals)

    if (indexValue) {
        def newConvertedPrice = rounding.round(productPrice * conversionFromPricingUOMToAdderUOM, numberOfDecimals)
        adder = newConvertedPrice - indexValue
    }
    deliveredPrice = shouldAddFreight ? productPrice + (freightAmount * conversionFromFreightUOMToPricingUOM) : productPrice
} else if (overriddenValue == "DeliveredPrice") {
    api.local.manualOverrideWarnings.add("New Delivered Price was manually changed, New Adder and New Product Price cannot be changed manually")

    api.removeManualOverride("NewAdder")
    api.removeManualOverride("NewProductPrice")

    deliveredPrice = rounding.round(api.getManualOverride("NewDeliveredPrice"), numberOfDecimals)

    productPrice = shouldAddFreight ? deliveredPrice - (freightAmount * conversionFromFreightUOMToPricingUOM) : deliveredPrice
    productPrice = rounding.round(productPrice, numberOfDecimals)
    if (indexValue) {
        def newConvertedPrice = rounding.round(productPrice * conversionFromPricingUOMToAdderUOM, numberOfDecimals)
        adder = newConvertedPrice - indexValue
    }
} else if (api.getManualOverride("NewFreightAmount")) {
    productPrice = oldProductPrice

    if (indexValue) {
        def newConvertedPrice = rounding.round(productPrice * conversionFromPricingUOMToAdderUOM, numberOfDecimals)
        adder = newConvertedPrice - indexValue
    }
    deliveredPrice = shouldAddFreight ? productPrice + (freightAmount * conversionFromFreightUOMToPricingUOM) : productPrice
} else {
    return [:]
}
return [
        OverridenValue: overriddenValue,
        Adder         : adder,
        ProductPrice  : productPrice,
        FreightAmount : freightAmount,
        DeliveredPrice: deliveredPrice,
]