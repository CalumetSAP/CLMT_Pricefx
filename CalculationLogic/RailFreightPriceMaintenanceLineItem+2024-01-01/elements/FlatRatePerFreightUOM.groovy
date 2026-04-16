if (out.HaulCharges == null) return null

def flatRate = api.local.flatRatePerMOQ ?: BigDecimal.ZERO
BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(api.local.sku, out.MOQUOM, out.FreightUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()

if (!conversionFactor) {
    api.local.conversionAlerts.add("Missing 'UOM conversion' from MOQ UOM (${out.MOQUOM}) to Freight UOM (${out.FreightUOM}) for material ${api.local.sku}")
    return null
}

def value = flatRate * conversionFactor

return value