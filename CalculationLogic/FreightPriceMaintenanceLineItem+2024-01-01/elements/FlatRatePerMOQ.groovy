def average = out.AverageLinehaulRate
if (!average) return null

def flatRate = api.local.flatRateQuotedToCustomer ?: BigDecimal.ZERO
def moq = out.MOQ

if (!moq) return null

def value = flatRate / moq

api.local.flatRatePerMOQ = value

return value