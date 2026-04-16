def average = out.AverageLinehaulRate
if (!average) return null

def flatRate = out.FlatRatePerFreightUOM ?: BigDecimal.ZERO
def freightAmount = out.LoadQuotes.FreightAmount ?: BigDecimal.ZERO

def value = flatRate - freightAmount
value = libs.SharedLib.RoundingUtils.round(value, 2)

return value