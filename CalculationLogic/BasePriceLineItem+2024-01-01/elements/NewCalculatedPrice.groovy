def newListPrice = out.NewListPrice?.toBigDecimal() ?: BigDecimal.ZERO
def discount = out.Discount?.toBigDecimal() ?: BigDecimal.ZERO

return newListPrice * (1-discount)