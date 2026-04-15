def newPrice
def newListPriceZLISOverride = api.local.newListPriceZLISOverride
if (newListPriceZLISOverride) {
    newPrice = newListPriceZLISOverride.overriddenValue
} else {
    newPrice = out.NewListPriceValue ?: api.local.currentContext?.get("NewListPrice")
    if (api.local.newListPriceError) {
        api.criticalAlert(api.local.newListPriceError as String)
    }
}

return api.attributedResult(newPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())