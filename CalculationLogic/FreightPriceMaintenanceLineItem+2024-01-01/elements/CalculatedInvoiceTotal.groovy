def average = out.AverageLinehaulRate
if (!average) return null

def averageRate = api.local.averageLinehaulRate ?: BigDecimal.ZERO
def tankWashFees = out.TankWashFees ?: BigDecimal.ZERO
def destinationAssessorial = out.DestinationAssessorial ?: BigDecimal.ZERO
def fsc = out.FSC ?: BigDecimal.ZERO

def value = averageRate + tankWashFees + destinationAssessorial + fsc

api.local.calculatedInvoiceTotal = value

return value