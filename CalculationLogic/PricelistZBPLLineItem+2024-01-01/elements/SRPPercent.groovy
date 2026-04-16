BigDecimal srpPercent
def srpOverride = api.local.srpOverride

if (srpOverride) {
    srpPercent = srpOverride.overriddenValue
} else {
    srpPercent = out.CalculateSRPPercent// ?: api.local.currentContext?.get("SRPPercent")
}

return api.attributedResult(srpPercent).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())