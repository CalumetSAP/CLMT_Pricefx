if (out.HaulCharges == null) return null

def haulCharges = out.HaulCharges
def fsc = out.FSC ?: BigDecimal.ZERO

def value = haulCharges + fsc

api.local.calculatedInvoiceTotal = value

return value