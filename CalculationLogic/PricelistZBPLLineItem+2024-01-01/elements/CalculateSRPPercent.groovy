//This if is to avoid unnecessary calculation. "Fail Fast"
def srpOverride = api.local.srpOverride
if (srpOverride) return null

BigDecimal srpPercent

BigDecimal newSRPOverride = api.getManualOverride("NewSRP")?.toBigDecimal()
if (newSRPOverride != null) {
    srpPercent = libs.PricelistLib.Calculations.calculatePercent(out.NewJobberDealerPrice?.toBigDecimal(), newSRPOverride)
} else {
    srpPercent = out.LoadBasePricings?.SRPPercent?.toBigDecimal()
}

return srpPercent