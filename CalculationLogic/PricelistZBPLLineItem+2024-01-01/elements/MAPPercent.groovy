BigDecimal mapPercent
def mapOverride = api.local.mapOverride

if (mapOverride) {
    mapPercent = mapOverride.overriddenValue
} else {
    mapPercent = out.CalculateMAPPercent // ?: api.local.currentContext?.get("MAPPercent")
}

return api.attributedResult(mapPercent).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())