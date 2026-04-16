BigDecimal newSRPPrice = out.NewSRP?.toBigDecimal()
BigDecimal mapPercent = out.MAPPercent?.toBigDecimal()

BigDecimal newMAPPrice = null
if (mapPercent != null && newSRPPrice != null) {
    newMAPPrice = newSRPPrice * (1 - mapPercent)
    newMAPPrice = libs.SharedLib.RoundingUtils.round(newMAPPrice, 0)-0.01
}

return api.attributedResult(newMAPPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())