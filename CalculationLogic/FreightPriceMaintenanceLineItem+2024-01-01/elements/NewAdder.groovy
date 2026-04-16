def newAdder = out.ManageOverriddenValues.Adder ?: out.CalculatePrices.NewAdder

def canEdit = !api.getManualOverride("NewProductPrice") && !api.getManualOverride("NewDeliveredPrice")
def color = canEdit ? libs.PricelistLib.Colors.getEditableFieldColor() : libs.PricelistLib.Colors.getOverrideFieldColor()

return api.attributedResult(newAdder).withBackgroundColor(color)