//TODO waiting definition?
BigDecimal costAverage = out.LoadCosts?.average?.toBigDecimal()
String costUOM = out.LoadCosts?.uom
def listPriceUOM, uomFieldLabel
def per = out.Per

listPriceUOM = out.PriceUOM
uomFieldLabel = "ZLIS"

if (costAverage && costUOM && listPriceUOM && listPriceUOM != costUOM) {
    String material = api.local.material
    BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, costUOM, listPriceUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
    if (conversionFactor) {
        costAverage = costAverage * conversionFactor * per
    } else {
        api.criticalAlert("Missing conversion from Cost UOM (${costUOM}) to ${uomFieldLabel} UOM (${listPriceUOM}) for material ${material}")
        costAverage = null
    }
}

return costAverage