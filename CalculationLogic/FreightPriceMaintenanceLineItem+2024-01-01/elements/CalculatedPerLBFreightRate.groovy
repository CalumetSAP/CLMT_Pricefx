def average = out.AverageLinehaulRate
if (!average) return null

def flatRate = api.local.flatRatePerMOQ ?: BigDecimal.ZERO
BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(api.local.sku, out.MOQUOM, "LB", api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()

if (!conversionFactor) {
    api.local.conversionAlerts.add("Missing 'UOM conversion' from MOQ UOM (${out.MOQUOM}) to LB for material ${api.local.sku}")
    return null
}

def value = flatRate * conversionFactor

return value