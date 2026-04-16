def costAverage = out.LoadCosts?.costAverage

def itemCurrency = out.Currency
if (costAverage != null && itemCurrency == "EUR") {
    if (api.global.ccyExchangeRateUSDToEUR) {
        costAverage = costAverage * api.global.ccyExchangeRateUSDToEUR
    } else {
        api.criticalAlert("Missing exchange rate from USD to EUR for today's date")
        costAverage = null
    }
}

if (costAverage != null) {
    BigDecimal per = out.Per?.toBigDecimal()
    costAverage = costAverage * per

    String costUOM = out.LoadCosts?.uom
    String pricingUOM = out.PricingUOM
    if (costUOM && pricingUOM && pricingUOM != costUOM) {
        String material = api.local.material
        BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, costUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
        if (conversionFactor) {
            costAverage = costAverage * conversionFactor
        } else {
            api.criticalAlert("Missing conversion from Cost UOM (${costUOM}) to Price UOM (${pricingUOM}) for material ${material}")
            costAverage = null
        }
    }
    costAverage = out.NumberOfDecimals ? libs.SharedLib.RoundingUtils.round(costAverage, out.NumberOfDecimals) : costAverage
}

return costAverage