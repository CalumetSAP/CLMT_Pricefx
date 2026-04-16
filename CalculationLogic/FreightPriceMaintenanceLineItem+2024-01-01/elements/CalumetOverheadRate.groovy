def average = out.AverageLinehaulRate
if (!average) return null

def invoiceTotal = api.local.calculatedInvoiceTotal ?: BigDecimal.ZERO
def overheadAssessorialPercentTruck = out.LoadShippingPointCPT.OverheadAssessorialPercentTruck ?: BigDecimal.ZERO

def value = invoiceTotal * overheadAssessorialPercentTruck

api.local.calumetOverheadRate = value

return value