def average = out.AverageLinehaulRate
if (!average) return null

def assessorialsMap = api.global.assessorials as Map

def destinationCountry = out.DestinationCountry ?: ""
def border1 = assessorialsMap["BORDER 1"]?.get(destinationCountry)?.Rate ?: BigDecimal.ZERO
def border2 = assessorialsMap["BORDER 2"]?.get(destinationCountry)?.Rate ?: BigDecimal.ZERO

return border1 + border2