def average = out.AverageLinehaulRate
if (!average) return null

def invoiceTotal = api.local.calculatedInvoiceTotal ?: BigDecimal.ZERO
def calumetOverheadRate = api.local.calumetOverheadRate ?: BigDecimal.ZERO

def value = invoiceTotal + calumetOverheadRate

api.local.flatRateQuotedToCustomer = value

return value