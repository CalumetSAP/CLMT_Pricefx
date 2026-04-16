String adderUOM = api.local.adderUOM
BigDecimal newPriceOverride = api.getManualOverride("NewPrice")
def numberOfDecimals = out.NumberOfDecimals ?: "2"
if (newPriceOverride) {
    String material = api.local.material
    String pricingUOM = out.LoadQuotes.PricingUOM
    String productUOM = out.LoadProducts?.UOM
    BigDecimal pricingToProductConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, pricingUOM, productUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
    BigDecimal productToAdderConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, productUOM, adderUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()

    List<String> alerts = []
    if (!pricingToProductConversionFactor) {
        alerts.add("Missing 'UOM conversion' from Pricing UOM (${pricingUOM}) to Product UOM (${productUOM}) for material ${material}")
    }
    if (!productToAdderConversionFactor) {
        alerts.add("Missing 'UOM conversion' from Product UOM (${productUOM}) to Adder UOM (${adderUOM}) for material ${material}")
    }

    Map index1Calculation = api.local.index1Calculation
    Map index2Calculation = api.local.index2Calculation
    Map index3Calculation = api.local.index3Calculation
    if (index1Calculation?.conversionAlertMsgs || index2Calculation?.conversionAlertMsgs || index3Calculation?.conversionAlertMsgs) {
        alerts.add("Missing 'Global UOM conversion' for Index Value")
    }

    if (alerts) {
        api.criticalAlert(alerts.join("; "))
        return null
    } else {
        newPriceOverride = newPriceOverride * pricingToProductConversionFactor * productToAdderConversionFactor

        BigDecimal index1Value = index1Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
        BigDecimal index2Value = index2Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
        BigDecimal index3Value = index3Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
        BigDecimal index1Percent = api.local.index1Percent
        BigDecimal index2Percent = api.local.index2Percent
        BigDecimal index3Percent = api.local.index3Percent

        newPriceOverride = newPriceOverride - (index1Value * index1Percent) - (index2Value * index2Percent) - (index3Value * index3Percent)
        return api.attributedResult(libs.SharedLib.RoundingUtils.round(newPriceOverride, numberOfDecimals.toInteger()))
                .withBackgroundColor(libs.PricelistLib.Colors.getOverrideFieldColor())
    }
}

return api.attributedResult(libs.SharedLib.RoundingUtils.round(out.LoadQuotes?.Adder?.toBigDecimal(), numberOfDecimals.toInteger()))
        .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())