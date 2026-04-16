if (!out.NumberOfDecimals) return

List<String> alerts = []
String material = api.local.material

BigDecimal adder = api.local.adder ?: BigDecimal.ZERO
String adderUOM = api.local.adderUOM
String productUOM = out.LoadProducts?.UOM
String pricingUOM = out.LoadQuotes.PricingUOM

BigDecimal adderToProductConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, adderUOM, productUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
BigDecimal productToPricingConversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, productUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()

if (!adderToProductConversionFactor) {
    alerts.add("Missing 'UOM conversion' from Adder UOM (${adderUOM}) to Product UOM (${productUOM}) for material ${material}")
}
if (!productToPricingConversionFactor) {
    alerts.add("Missing 'UOM conversion' from Product UOM (${productUOM}) to Pricing UOM (${pricingUOM}) for material ${material}")
}

Map index1Calculation = api.local.index1Calculation
Map index2Calculation = api.local.index2Calculation
Map index3Calculation = api.local.index3Calculation
if (index1Calculation?.conversionAlertMsgs || index2Calculation?.conversionAlertMsgs || index3Calculation?.conversionAlertMsgs) {
    alerts.add("Missing 'Global UOM conversion' for Index Value")
}

BigDecimal newPrice = null
if (alerts) {
    api.criticalAlert(alerts.join("; "))
} else {
    BigDecimal index1Value = index1Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
    BigDecimal index2Value = index2Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
    BigDecimal index3Value = index3Calculation?.indexValueInAdderUOM?.toBigDecimal() ?: BigDecimal.ZERO
    BigDecimal index1Percent = api.local.index1Percent
    BigDecimal index2Percent = api.local.index2Percent
    BigDecimal index3Percent = api.local.index3Percent

    newPrice = (index1Value * index1Percent) + (index2Value * index2Percent) + (index3Value * index3Percent) + adder
    newPrice = newPrice * adderToProductConversionFactor * productToPricingConversionFactor

    newPrice = libs.SharedLib.RoundingUtils.round(newPrice, out.NumberOfDecimals)
}

return api.attributedResult(newPrice)
        .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())