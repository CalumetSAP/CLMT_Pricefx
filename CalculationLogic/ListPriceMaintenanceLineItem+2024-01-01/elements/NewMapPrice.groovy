BigDecimal newSRPPrice = out.NewSRP?.toBigDecimal()
BigDecimal mapPercent = out.MAPPercent?.toBigDecimal()

BigDecimal newMAPPrice = null
if (mapPercent != null && newSRPPrice != null) {
    newMAPPrice = newSRPPrice * (1 - mapPercent)
}

return api.attributedResult(newMAPPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())