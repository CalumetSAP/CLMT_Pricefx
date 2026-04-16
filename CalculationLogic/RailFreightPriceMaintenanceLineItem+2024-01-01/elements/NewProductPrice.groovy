def newProductPrice = out.ManageOverriddenValues.ProductPrice ?: out.CalculatePrices.NewProductPrice
newProductPrice = out.NumberOfDecimals ? libs.SharedLib.RoundingUtils.round(newProductPrice, out.NumberOfDecimals) : newProductPrice

def canEdit = !api.getManualOverride("NewDeliveredPrice")
def color = canEdit ? libs.PricelistLib.Colors.getEditableFieldColor() : libs.PricelistLib.Colors.getOverrideFieldColor()

return api.attributedResult(newProductPrice).withBackgroundColor(color)