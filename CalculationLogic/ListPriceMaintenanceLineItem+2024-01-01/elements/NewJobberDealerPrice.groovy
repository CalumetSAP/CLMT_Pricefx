BigDecimal newBasePriceZBPL = out.NewBasePrice?.toBigDecimal()
def listPriceUOM, uomFieldLabel
if (out.BaseUOM) {
    listPriceUOM = out.BaseUOM
    uomFieldLabel = "ZBPL"
} else {
    listPriceUOM = out.PriceUOM
    uomFieldLabel = "ZLIS"
}
String eaUOM = "EA"
BigDecimal jobberPercent = out.JobberDealerPercent?.toBigDecimal()
BigDecimal newJobberPrice = null
if (jobberPercent != null && jobberPercent != 1 && newBasePriceZBPL != null && listPriceUOM && listPriceUOM != eaUOM) {
    String material = api.local.material
    BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, listPriceUOM, eaUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
    if (conversionFactor) {
        newBasePriceZBPL = newBasePriceZBPL * conversionFactor
        newJobberPrice = newBasePriceZBPL / (1 - jobberPercent)
    } else {
        api.criticalAlert("Missing conversion from ${uomFieldLabel} UOM (${listPriceUOM}) to EA for material ${material}")
    }
}

return api.attributedResult(newJobberPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())