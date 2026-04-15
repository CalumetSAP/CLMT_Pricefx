def rateRows = out.LoadTruckRateUploadPX.RateRows

if (!rateRows) return null

def rates = rateRows.collect { it.Rate as BigDecimal }
BigDecimal avgRate = rates ? (rates.sum() / rates.size()) : null

api.local.averageLinehaulRate = avgRate

return avgRate