if (out.HaulCharges == null) return null

def invoiceTotal = api.local.calculatedInvoiceTotal ?: BigDecimal.ZERO
def calumetOverheadRate = api.local.calumetOverheadRate ?: BigDecimal.ZERO

def value = invoiceTotal + calumetOverheadRate

api.local.flatRateQuotedToCustomer = value

return value