//TODO waiting definition?
BigDecimal costAverage = out.LoadCosts?.average?.toBigDecimal()
String costUOM = out.LoadCosts?.uom
def listPriceUOM, uomFieldLabel
if (out.BaseUOM) {
    listPriceUOM = out.BaseUOM
    uomFieldLabel = "ZBPL"
} else {
    listPriceUOM = out.PriceUOM
    uomFieldLabel = "ZLIS"
}

if (costAverage && costUOM && listPriceUOM && listPriceUOM != costUOM) {
    String material = api.local.material
    BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, costUOM, listPriceUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
    if (conversionFactor) {
        costAverage = costAverage * conversionFactor
    } else {
        api.criticalAlert("Missing conversion from Cost UOM (${costUOM}) to ${uomFieldLabel} UOM (${listPriceUOM}) for material ${material}")
        costAverage = null
    }
}

return costAverage