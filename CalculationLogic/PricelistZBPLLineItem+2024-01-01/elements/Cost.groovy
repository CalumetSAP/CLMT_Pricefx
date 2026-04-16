BigDecimal costAverage = out.LoadCosts?.average?.toBigDecimal()

if (costAverage != null) {
    BigDecimal per = out.Per?.toBigDecimal()
    costAverage = costAverage * per

    String costUOM = out.LoadCosts?.uom
    String listPriceUOM = out.BaseUOM
    if (costUOM && listPriceUOM && listPriceUOM != costUOM) {
        String material = api.local.material
        BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, costUOM, listPriceUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
        if (conversionFactor) {
            costAverage = costAverage * conversionFactor
        } else {
            api.criticalAlert("Missing conversion from Cost UOM (${costUOM}) to ZBPL UOM (${listPriceUOM}) for material ${material}")
            costAverage = null
        }
    }
}

return costAverage