//This if is to avoid unnecessary calculation. "Fail Fast"
def jobberDealerOverride = api.local.jobberDealerOverride

if (jobberDealerOverride) return null

BigDecimal newJobberDealerPriceOverride = api.getManualOverride("NewJobberDealerPrice")?.toBigDecimal()
BigDecimal jobberPercent
if (newJobberDealerPriceOverride != null) {
    String listPriceUOM = out.BaseUOM
    String eaUOM = "EA"
    if (newJobberDealerPriceOverride && listPriceUOM && listPriceUOM != eaUOM) {
        String material = api.local.material
        BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, eaUOM, listPriceUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
        if (conversionFactor) {
            newJobberDealerPriceOverride = newJobberDealerPriceOverride * conversionFactor
        } else {
            api.local.jobberDealerPercentError = "Missing conversion from EA to ZBPL UOM (${listPriceUOM}) for material ${material}"
            newJobberDealerPriceOverride = null
        }
    }
    jobberPercent = libs.PricelistLib.Calculations.calculatePercent(out.NewBasePrice?.toBigDecimal(), newJobberDealerPriceOverride)
} else {
    jobberPercent = out.LoadBasePricings?.JobberPercent?.toBigDecimal()
}

return jobberPercent