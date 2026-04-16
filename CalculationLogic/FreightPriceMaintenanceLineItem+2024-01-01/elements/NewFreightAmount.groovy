def newFreightAmount = out.ManageOverriddenValues.FreightAmount ?: out.CalculatePrices.NewFreightAmount
newFreightAmount = libs.SharedLib.RoundingUtils.round(newFreightAmount, 2)

return api.attributedResult(newFreightAmount).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())