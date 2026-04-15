//This if is to avoid unnecessary calculation. "Fail Fast"
def mapOverride = api.local.mapOverride
if (mapOverride) return null

BigDecimal mapPercent

BigDecimal newMapPriceOverride = api.getManualOverride("NewMapPrice")?.toBigDecimal()
if (newMapPriceOverride != null) {
    mapPercent = libs.PricelistLib.Calculations.calculateInversePercent(out.NewSRP?.toBigDecimal(), newMapPriceOverride)
} else {
    mapPercent = out.LoadBasePricings?.MAPPercent?.toBigDecimal()
}

return mapPercent