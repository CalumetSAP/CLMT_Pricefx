BigDecimal newJobberDealerPrice = out.NewJobberDealerPrice?.toBigDecimal()
BigDecimal srpPercent = out.SRPPercent?.toBigDecimal()

BigDecimal newSRPPrice = null
if (srpPercent != null && srpPercent != 1 && newJobberDealerPrice != null) {
    newSRPPrice = newJobberDealerPrice / (1 - srpPercent)
    newSRPPrice = libs.SharedLib.RoundingUtils.round(newSRPPrice, 0)-0.01
}

return api.attributedResult(newSRPPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())