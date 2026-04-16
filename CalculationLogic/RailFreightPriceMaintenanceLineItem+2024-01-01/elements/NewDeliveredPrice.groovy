def newDeliveredPrice = out.ManageOverriddenValues.DeliveredPrice ?: out.CalculatePrices.NewDeliveredPrice
newDeliveredPrice = out.NumberOfDecimals ? libs.SharedLib.RoundingUtils.round(newDeliveredPrice, out.NumberOfDecimals) : newDeliveredPrice

def canEdit = !api.getManualOverride("NewAdder") && !api.getManualOverride("NewProductPrice")
def color = canEdit ? libs.PricelistLib.Colors.getEditableFieldColor() : libs.PricelistLib.Colors.getOverrideFieldColor()

return api.attributedResult(newDeliveredPrice).withBackgroundColor(color)